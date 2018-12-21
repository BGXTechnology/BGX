/*
 * Copyright 2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------
 */

use std::ffi::CStr;
use std::mem;
use std::os::raw::{c_char, c_void};
use std::slice;

use block::Block;
use cpython::{PyObject, Python};
use journal::block_manager::{
    BlockManager, BlockManagerError, BranchDiffIterator, BranchIterator, GetBlockIterator,
};
use journal::chain_ffi::PyBlockStore;
use proto;
use protobuf::{self, Message};
use py_ffi;

#[repr(u32)]
#[derive(Debug)]
pub enum ErrorCode {
    Success = 0,
    NullPointerProvided = 0x01,
    MissingPredecessor = 0x02,
    MissingPredecessorInBranch = 0x03,
    MissingInput = 0x04,
    UnknownBlock = 0x05,
    InvalidInputString = 0x06,
    Error = 0x07,
    InvalidPythonObject = 0x10,
    StopIteration = 0x11,
}

macro_rules! check_null {
    ($($arg:expr) , *) => {
        $(if $arg.is_null() { return ErrorCode::NullPointerProvided; })*
    }
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_new(block_manager_ptr: *mut *const c_void) -> ErrorCode {
    let block_manager = BlockManager::new();

    *block_manager_ptr = Box::into_raw(Box::new(block_manager)) as *const c_void;

    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_drop(block_manager: *mut c_void) -> ErrorCode {
    check_null!(block_manager);
    Box::from_raw(block_manager as *mut BlockManager);
    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_contains(
    block_manager: *mut c_void,
    block_id: *const c_char,
    result: *mut bool,
) -> ErrorCode {
    check_null!(block_manager, block_id);

    let block_id = match CStr::from_ptr(block_id).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };

    match (*(block_manager as *mut BlockManager)).contains(block_id) {
        Ok(contains) => {
            *result = contains;
            ErrorCode::Success
        }
        Err(err) => {
            error!("Unexpected error calling BlockManager.contains: {:?}", err);
            ErrorCode::Error
        }
    }
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_add_store(
    block_manager: *mut c_void,
    block_store_name: *const c_char,
    block_store: *mut py_ffi::PyObject,
) -> ErrorCode {
    check_null!(block_manager, block_store_name, block_store);

    let gil = Python::acquire_gil();
    let py = gil.python();
    let py_block_store = PyBlockStore::new(PyObject::from_borrowed_ptr(py, block_store));

    let name = match CStr::from_ptr(block_store_name).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };

    let block_manager = (*(block_manager as *mut BlockManager)).clone();

    py.allow_threads(move || {
        block_manager
            .add_store(name, Box::new(py_block_store))
            .map(|_| ErrorCode::Success)
            .unwrap_or(ErrorCode::Error)
    })
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_persist(
    block_manager: *mut c_void,
    block_id: *const c_char,
    store_name: *const c_char,
) -> ErrorCode {
    check_null!(block_manager, block_id, store_name);

    let block_id = match CStr::from_ptr(block_id).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };
    let name = match CStr::from_ptr(store_name).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };

    (*(block_manager as *mut BlockManager))
        .persist(block_id, name)
        .map(|_| ErrorCode::Success)
        .map_err(|err| {
            error!("Unexpected error calling BlockManager.persist: {:?}", err);
            err
        }).unwrap_or(ErrorCode::Error)
}

#[repr(C)]
#[derive(Debug)]
pub struct PutEntry {
    block_bytes: *mut u8,
    block_bytes_len: usize,
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_put(
    block_manager: *mut c_void,
    branch: *const *const c_void,
    branch_len: usize,
) -> ErrorCode {
    check_null!(block_manager, branch);

    let branch_result: Result<Vec<Block>, ErrorCode> = slice::from_raw_parts(branch, branch_len)
        .into_iter()
        .map(|ptr| {
            let entry = *ptr as *const PutEntry;
            let payload = slice::from_raw_parts((*entry).block_bytes, (*entry).block_bytes_len);
            let proto_block: proto::block::Block =
                protobuf::parse_from_bytes(&payload).expect("Failed to parse proto Block bytes");

            Ok(Block::from(proto_block))
        }).collect();

    match branch_result {
        Ok(branch) => match (*(block_manager as *mut BlockManager)).put(branch) {
            Err(BlockManagerError::MissingPredecessor(_)) => ErrorCode::MissingPredecessor,
            Err(BlockManagerError::MissingInput) => ErrorCode::MissingInput,
            Err(BlockManagerError::MissingPredecessorInBranch(_)) => {
                ErrorCode::MissingPredecessorInBranch
            }
            Err(_) => ErrorCode::Error,
            Ok(_) => ErrorCode::Success,
        },
        Err(err) => {
            error!("Error processing branch: {:?}", err);
            ErrorCode::Error
        }
    }
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_ref_block(
    block_manager: *mut c_void,
    block_id: *const c_char,
) -> ErrorCode {
    check_null!(block_manager, block_id);

    let block_id = match CStr::from_ptr(block_id).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };

    match (*(block_manager as *mut BlockManager)).ref_block(block_id) {
        Ok(()) => ErrorCode::Success,
        Err(BlockManagerError::UnknownBlock) => ErrorCode::UnknownBlock,
        Err(err) => {
            error!("Unexpected error while ref'ing block: {:?}", err);
            ErrorCode::Error
        }
    }
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_unref_block(
    block_manager: *mut c_void,
    block_id: *const c_char,
) -> ErrorCode {
    check_null!(block_manager, block_id);

    let block_id = match CStr::from_ptr(block_id).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidInputString,
    };

    match (*(block_manager as *mut BlockManager)).unref_block(block_id) {
        Ok(_) => ErrorCode::Success,
        Err(BlockManagerError::UnknownBlock) => ErrorCode::UnknownBlock,
        Err(err) => {
            error!("Unexpected error while unref'ing block: {:?}", err);
            ErrorCode::Error
        }
    }
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_get_iterator_new(
    block_manager: *mut c_void,
    block_ids: *const *const c_char,
    block_ids_len: usize,
    iterator: *mut *const c_void,
) -> ErrorCode {
    check_null!(block_manager, block_ids);

    let block_ids = match slice::from_raw_parts(block_ids, block_ids_len)
        .iter()
        .map(|c_str| CStr::from_ptr(*c_str).to_str())
        .collect::<Result<Vec<&str>, _>>()
    {
        Ok(ids) => ids,
        Err(_) => return ErrorCode::InvalidPythonObject,
    };

    *iterator =
        Box::into_raw((*(block_manager as *mut BlockManager)).get(&block_ids)) as *const c_void;

    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_get_iterator_drop(iterator: *mut c_void) -> ErrorCode {
    check_null!(iterator);

    Box::from_raw(iterator as *mut GetBlockIterator);
    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_get_iterator_next(
    iterator: *mut c_void,
    block_bytes: *mut *const u8,
    block_len: *mut usize,
    block_cap: *mut usize,
) -> ErrorCode {
    check_null!(iterator);

    if let Some(Some(block)) = (*(iterator as *mut GetBlockIterator)).next() {
        let proto_block: proto::block::Block = block.into();
        let bytes = proto_block
            .write_to_bytes()
            .expect("Failed to serialize proto Block");

        *block_cap = bytes.capacity();
        *block_len = bytes.len();
        *block_bytes = bytes.as_slice().as_ptr();

        mem::forget(bytes);

        return ErrorCode::Success;
    }

    ErrorCode::StopIteration
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_iterator_new(
    block_manager: *mut c_void,
    tip: *const c_char,
    iterator: *mut *const c_void,
) -> ErrorCode {
    check_null!(block_manager, tip);

    let tip = match CStr::from_ptr(tip).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidPythonObject,
    };
    match (*(block_manager as *mut BlockManager)).branch(tip) {
        Ok(branch) => *iterator = Box::into_raw(branch) as *const c_void,
        Err(BlockManagerError::UnknownBlock) => return ErrorCode::UnknownBlock,
        Err(_) => return ErrorCode::Error,
    }

    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_iterator_drop(iterator: *mut c_void) -> ErrorCode {
    check_null!(iterator);
    Box::from_raw(iterator as *mut BranchIterator);
    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_iterator_next(
    iterator: *mut c_void,
    block_bytes: *mut *const u8,
    block_len: *mut usize,
    block_cap: *mut usize,
) -> ErrorCode {
    check_null!(iterator);

    if let Some(block) = (*(iterator as *mut BranchIterator)).next() {
        let proto_block: proto::block::Block = block.into();
        let bytes = proto_block
            .write_to_bytes()
            .expect("Failed to serialize proto Block");

        *block_cap = bytes.capacity();
        *block_len = bytes.len();
        *block_bytes = bytes.as_slice().as_ptr();

        mem::forget(bytes);

        return ErrorCode::Success;
    }

    ErrorCode::StopIteration
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_diff_iterator_new(
    block_manager: *mut c_void,
    tip: *const c_char,
    exclude: *const c_char,
    iterator: *mut *const c_void,
) -> ErrorCode {
    check_null!(block_manager, tip, exclude);

    let tip = match CStr::from_ptr(tip).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidPythonObject,
    };

    let exclude = match CStr::from_ptr(exclude).to_str() {
        Ok(s) => s,
        Err(_) => return ErrorCode::InvalidPythonObject,
    };
    match (*(block_manager as *mut BlockManager)).branch_diff(tip, exclude) {
        Ok(branch_diff) => *iterator = Box::into_raw(branch_diff) as *const c_void,
        Err(BlockManagerError::UnknownBlock) => return ErrorCode::UnknownBlock,
        Err(_) => return ErrorCode::Error,
    }

    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_diff_iterator_drop(
    iterator: *mut c_void,
) -> ErrorCode {
    check_null!(iterator);
    Box::from_raw(iterator as *mut BranchDiffIterator);
    ErrorCode::Success
}

#[no_mangle]
pub unsafe extern "C" fn block_manager_branch_diff_iterator_next(
    iterator: *mut c_void,
    block_bytes: *mut *const u8,
    block_len: *mut usize,
    block_cap: *mut usize,
) -> ErrorCode {
    check_null!(iterator);

    if let Some(block) = (*(iterator as *mut BranchDiffIterator)).next() {
        let proto_block: proto::block::Block = block.into();
        let bytes = proto_block
            .write_to_bytes()
            .expect("Failed to serialize proto Block");

        *block_cap = bytes.capacity();
        *block_len = bytes.len();
        *block_bytes = bytes.as_slice().as_ptr();

        mem::forget(bytes);

        return ErrorCode::Success;
    }

    ErrorCode::StopIteration
}

#[cfg(test)]
mod test {
    use super::*;
    use block::Block;
    use journal::block_store::BlockStore;
    use journal::NULL_BLOCK_IDENTIFIER;
    use proto::block::BlockHeader;

    use cpython::{NoArgs, ObjectProtocol, PyClone, PyObject, Python};

    use protobuf;
    use protobuf::Message;

    use std::env;
    use std::fs::remove_file;
    use std::panic;
    use std::thread;

    const TEST_DB_SIZE: usize = 10 * 1024 * 1024;

    /// This test matches the basic test in journal::block_store.
    /// It creates a Python-backed block store, inserts several blocks,
    /// retrieves them, iterates on them, and deletes one.
    #[test]
    fn py_block_store() {
        run_test(|db_path| {
            let py_block_store = create_block_store(db_path);
            let mut store = {
                let gil_guard = Python::acquire_gil();
                let py = gil_guard.python();
                PyBlockStore::new(py_block_store.clone_ref(py))
            };

            let block_a = create_block("A", 1, NULL_BLOCK_IDENTIFIER);
            let block_b = create_block("B", 2, "A");
            let block_c = create_block("C", 3, "B");

            store
                .put(vec![block_a.clone(), block_b.clone(), block_c.clone()])
                .unwrap();
            assert_eq!(store.get(&["A"]).unwrap().next().unwrap(), block_a);

            {
                let mut iterator = store.iter().unwrap();

                assert_eq!(iterator.next().unwrap(), block_c);
                assert_eq!(iterator.next().unwrap(), block_b);
                assert_eq!(iterator.next().unwrap(), block_a);
                assert_eq!(iterator.next(), None);
            }

            assert_eq!(store.delete(&["C"]).unwrap(), vec![block_c.clone()]);

            let chain_head = get_chain_head(&py_block_store);

            assert_eq!(block_b, chain_head);
        })
    }

    #[test]
    fn persist_to_py_block_store() {
        run_test(|db_path| {
            let py_block_store = create_block_store(db_path);
            let mut store = {
                let gil_guard = Python::acquire_gil();
                let py = gil_guard.python();
                PyBlockStore::new(py_block_store.clone_ref(py))
            };

            let block_manager = BlockManager::new();
            block_manager.add_store("commit_store", Box::new(store.clone()));

            let block_a = create_block("A", 1, NULL_BLOCK_IDENTIFIER);
            let block_b = create_block("B", 2, "A");
            let block_c = create_block("C", 3, "B");

            block_manager
                .put(vec![block_a.clone(), block_b.clone(), block_c.clone()])
                .unwrap();
            block_manager
                .persist(&block_c.header_signature, "commit_store")
                .unwrap();

            assert_eq!(store.get(&["A"]).unwrap().next().unwrap(), block_a);

            {
                let mut iterator = store.iter().unwrap();

                assert_eq!(iterator.next().unwrap(), block_c);
                assert_eq!(iterator.next().unwrap(), block_b);
                assert_eq!(iterator.next().unwrap(), block_a);
                assert_eq!(iterator.next(), None);
            }

            let chain_head = get_chain_head(&py_block_store);

            assert_eq!(block_c, chain_head);
        })
    }

    fn create_block(header_signature: &str, block_num: u64, previous_block_id: &str) -> Block {
        let mut block_header = BlockHeader::new();
        block_header.set_previous_block_id(previous_block_id.to_string());
        block_header.set_block_num(block_num);
        block_header.set_state_root_hash(format!("state-{}", block_num));
        Block {
            header_signature: header_signature.into(),
            previous_block_id: block_header.get_previous_block_id().to_string(),
            block_num,
            batches: vec![],
            state_root_hash: block_header.get_state_root_hash().to_string(),
            consensus: vec![],
            batch_ids: vec![],
            signer_public_key: "".into(),
            header_bytes: block_header.write_to_bytes().unwrap(),
        }
    }

    fn create_block_store(db_path: &str) -> PyObject {
        let gil_guard = Python::acquire_gil();
        let py = gil_guard.python();

        let block_store_mod = py.import("sawtooth_validator.journal.block_store").unwrap();
        let block_store = block_store_mod.get(py, "BlockStore").unwrap();

        let db_mod = py
            .import("sawtooth_validator.database.indexed_database")
            .unwrap();
        let indexed_database = db_mod.get(py, "IndexedDatabase").unwrap();

        let db_instance = indexed_database
            .call(
                py,
                (
                    db_path,
                    block_store.getattr(py, "serialize_block").unwrap(),
                    block_store.getattr(py, "deserialize_block").unwrap(),
                    block_store
                        .call_method(py, "create_index_configuration", NoArgs, None)
                        .unwrap(),
                    "c",
                    TEST_DB_SIZE,
                ),
                None,
            ).unwrap();

        block_store.call(py, (db_instance,), None).unwrap()
    }

    fn get_chain_head(py_block_store: &PyObject) -> Block {
        let gil_guard = Python::acquire_gil();
        let py = gil_guard.python();
        py_block_store
            .getattr(py, "chain_head")
            .map_err(|py_err| py_err.print(py))
            .unwrap()
            .getattr(py, "block")
            .unwrap()
            .extract(py)
            .unwrap()
    }

    fn run_test<T>(test: T) -> ()
    where
        T: FnOnce(&str) -> () + panic::UnwindSafe,
    {
        let dbpath = temp_db_path();

        let testpath = dbpath.clone();
        let result = panic::catch_unwind(move || test(&testpath));

        remove_file(dbpath).unwrap();

        assert!(result.is_ok())
    }

    fn temp_db_path() -> String {
        let mut temp_dir = env::temp_dir();

        let thread_id = thread::current().id();
        temp_dir.push(format!("merkle-{:?}.lmdb", thread_id));
        temp_dir.to_str().unwrap().to_string()
    }
}
