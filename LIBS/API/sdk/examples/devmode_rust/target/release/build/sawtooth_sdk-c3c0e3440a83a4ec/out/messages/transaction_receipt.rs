// This file is generated by rust-protobuf 2.0.4. Do not edit
// @generated

// https://github.com/Manishearth/rust-clippy/issues/702
#![allow(unknown_lints)]
#![allow(clippy)]

#![cfg_attr(rustfmt, rustfmt_skip)]

#![allow(box_pointers)]
#![allow(dead_code)]
#![allow(missing_docs)]
#![allow(non_camel_case_types)]
#![allow(non_snake_case)]
#![allow(non_upper_case_globals)]
#![allow(trivial_casts)]
#![allow(unsafe_code)]
#![allow(unused_imports)]
#![allow(unused_results)]

use protobuf::Message as Message_imported_for_functions;
use protobuf::ProtobufEnum as ProtobufEnum_imported_for_functions;

#[derive(PartialEq,Clone,Default)]
pub struct TransactionReceipt {
    // message fields
    pub state_changes: ::protobuf::RepeatedField<StateChange>,
    pub events: ::protobuf::RepeatedField<super::events::Event>,
    pub data: ::protobuf::RepeatedField<::std::vec::Vec<u8>>,
    pub transaction_id: ::std::string::String,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl TransactionReceipt {
    pub fn new() -> TransactionReceipt {
        ::std::default::Default::default()
    }

    // repeated .StateChange state_changes = 1;

    pub fn clear_state_changes(&mut self) {
        self.state_changes.clear();
    }

    // Param is passed by value, moved
    pub fn set_state_changes(&mut self, v: ::protobuf::RepeatedField<StateChange>) {
        self.state_changes = v;
    }

    // Mutable pointer to the field.
    pub fn mut_state_changes(&mut self) -> &mut ::protobuf::RepeatedField<StateChange> {
        &mut self.state_changes
    }

    // Take field
    pub fn take_state_changes(&mut self) -> ::protobuf::RepeatedField<StateChange> {
        ::std::mem::replace(&mut self.state_changes, ::protobuf::RepeatedField::new())
    }

    pub fn get_state_changes(&self) -> &[StateChange] {
        &self.state_changes
    }

    // repeated .Event events = 2;

    pub fn clear_events(&mut self) {
        self.events.clear();
    }

    // Param is passed by value, moved
    pub fn set_events(&mut self, v: ::protobuf::RepeatedField<super::events::Event>) {
        self.events = v;
    }

    // Mutable pointer to the field.
    pub fn mut_events(&mut self) -> &mut ::protobuf::RepeatedField<super::events::Event> {
        &mut self.events
    }

    // Take field
    pub fn take_events(&mut self) -> ::protobuf::RepeatedField<super::events::Event> {
        ::std::mem::replace(&mut self.events, ::protobuf::RepeatedField::new())
    }

    pub fn get_events(&self) -> &[super::events::Event] {
        &self.events
    }

    // repeated bytes data = 3;

    pub fn clear_data(&mut self) {
        self.data.clear();
    }

    // Param is passed by value, moved
    pub fn set_data(&mut self, v: ::protobuf::RepeatedField<::std::vec::Vec<u8>>) {
        self.data = v;
    }

    // Mutable pointer to the field.
    pub fn mut_data(&mut self) -> &mut ::protobuf::RepeatedField<::std::vec::Vec<u8>> {
        &mut self.data
    }

    // Take field
    pub fn take_data(&mut self) -> ::protobuf::RepeatedField<::std::vec::Vec<u8>> {
        ::std::mem::replace(&mut self.data, ::protobuf::RepeatedField::new())
    }

    pub fn get_data(&self) -> &[::std::vec::Vec<u8>] {
        &self.data
    }

    // string transaction_id = 4;

    pub fn clear_transaction_id(&mut self) {
        self.transaction_id.clear();
    }

    // Param is passed by value, moved
    pub fn set_transaction_id(&mut self, v: ::std::string::String) {
        self.transaction_id = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_transaction_id(&mut self) -> &mut ::std::string::String {
        &mut self.transaction_id
    }

    // Take field
    pub fn take_transaction_id(&mut self) -> ::std::string::String {
        ::std::mem::replace(&mut self.transaction_id, ::std::string::String::new())
    }

    pub fn get_transaction_id(&self) -> &str {
        &self.transaction_id
    }
}

impl ::protobuf::Message for TransactionReceipt {
    fn is_initialized(&self) -> bool {
        for v in &self.state_changes {
            if !v.is_initialized() {
                return false;
            }
        };
        for v in &self.events {
            if !v.is_initialized() {
                return false;
            }
        };
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_repeated_message_into(wire_type, is, &mut self.state_changes)?;
                },
                2 => {
                    ::protobuf::rt::read_repeated_message_into(wire_type, is, &mut self.events)?;
                },
                3 => {
                    ::protobuf::rt::read_repeated_bytes_into(wire_type, is, &mut self.data)?;
                },
                4 => {
                    ::protobuf::rt::read_singular_proto3_string_into(wire_type, is, &mut self.transaction_id)?;
                },
                _ => {
                    ::protobuf::rt::read_unknown_or_skip_group(field_number, wire_type, is, self.mut_unknown_fields())?;
                },
            };
        }
        ::std::result::Result::Ok(())
    }

    // Compute sizes of nested messages
    #[allow(unused_variables)]
    fn compute_size(&self) -> u32 {
        let mut my_size = 0;
        for value in &self.state_changes {
            let len = value.compute_size();
            my_size += 1 + ::protobuf::rt::compute_raw_varint32_size(len) + len;
        };
        for value in &self.events {
            let len = value.compute_size();
            my_size += 1 + ::protobuf::rt::compute_raw_varint32_size(len) + len;
        };
        for value in &self.data {
            my_size += ::protobuf::rt::bytes_size(3, &value);
        };
        if !self.transaction_id.is_empty() {
            my_size += ::protobuf::rt::string_size(4, &self.transaction_id);
        }
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        for v in &self.state_changes {
            os.write_tag(1, ::protobuf::wire_format::WireTypeLengthDelimited)?;
            os.write_raw_varint32(v.get_cached_size())?;
            v.write_to_with_cached_sizes(os)?;
        };
        for v in &self.events {
            os.write_tag(2, ::protobuf::wire_format::WireTypeLengthDelimited)?;
            os.write_raw_varint32(v.get_cached_size())?;
            v.write_to_with_cached_sizes(os)?;
        };
        for v in &self.data {
            os.write_bytes(3, &v)?;
        };
        if !self.transaction_id.is_empty() {
            os.write_string(4, &self.transaction_id)?;
        }
        os.write_unknown_fields(self.get_unknown_fields())?;
        ::std::result::Result::Ok(())
    }

    fn get_cached_size(&self) -> u32 {
        self.cached_size.get()
    }

    fn get_unknown_fields(&self) -> &::protobuf::UnknownFields {
        &self.unknown_fields
    }

    fn mut_unknown_fields(&mut self) -> &mut ::protobuf::UnknownFields {
        &mut self.unknown_fields
    }

    fn as_any(&self) -> &::std::any::Any {
        self as &::std::any::Any
    }
    fn as_any_mut(&mut self) -> &mut ::std::any::Any {
        self as &mut ::std::any::Any
    }
    fn into_any(self: Box<Self>) -> ::std::boxed::Box<::std::any::Any> {
        self
    }

    fn descriptor(&self) -> &'static ::protobuf::reflect::MessageDescriptor {
        Self::descriptor_static()
    }

    fn new() -> TransactionReceipt {
        TransactionReceipt::new()
    }

    fn descriptor_static() -> &'static ::protobuf::reflect::MessageDescriptor {
        static mut descriptor: ::protobuf::lazy::Lazy<::protobuf::reflect::MessageDescriptor> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ::protobuf::reflect::MessageDescriptor,
        };
        unsafe {
            descriptor.get(|| {
                let mut fields = ::std::vec::Vec::new();
                fields.push(::protobuf::reflect::accessor::make_repeated_field_accessor::<_, ::protobuf::types::ProtobufTypeMessage<StateChange>>(
                    "state_changes",
                    |m: &TransactionReceipt| { &m.state_changes },
                    |m: &mut TransactionReceipt| { &mut m.state_changes },
                ));
                fields.push(::protobuf::reflect::accessor::make_repeated_field_accessor::<_, ::protobuf::types::ProtobufTypeMessage<super::events::Event>>(
                    "events",
                    |m: &TransactionReceipt| { &m.events },
                    |m: &mut TransactionReceipt| { &mut m.events },
                ));
                fields.push(::protobuf::reflect::accessor::make_repeated_field_accessor::<_, ::protobuf::types::ProtobufTypeBytes>(
                    "data",
                    |m: &TransactionReceipt| { &m.data },
                    |m: &mut TransactionReceipt| { &mut m.data },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeString>(
                    "transaction_id",
                    |m: &TransactionReceipt| { &m.transaction_id },
                    |m: &mut TransactionReceipt| { &mut m.transaction_id },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<TransactionReceipt>(
                    "TransactionReceipt",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static TransactionReceipt {
        static mut instance: ::protobuf::lazy::Lazy<TransactionReceipt> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const TransactionReceipt,
        };
        unsafe {
            instance.get(TransactionReceipt::new)
        }
    }
}

impl ::protobuf::Clear for TransactionReceipt {
    fn clear(&mut self) {
        self.clear_state_changes();
        self.clear_events();
        self.clear_data();
        self.clear_transaction_id();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for TransactionReceipt {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for TransactionReceipt {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

#[derive(PartialEq,Clone,Default)]
pub struct StateChange {
    // message fields
    pub address: ::std::string::String,
    pub value: ::std::vec::Vec<u8>,
    pub field_type: StateChange_Type,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl StateChange {
    pub fn new() -> StateChange {
        ::std::default::Default::default()
    }

    // string address = 1;

    pub fn clear_address(&mut self) {
        self.address.clear();
    }

    // Param is passed by value, moved
    pub fn set_address(&mut self, v: ::std::string::String) {
        self.address = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_address(&mut self) -> &mut ::std::string::String {
        &mut self.address
    }

    // Take field
    pub fn take_address(&mut self) -> ::std::string::String {
        ::std::mem::replace(&mut self.address, ::std::string::String::new())
    }

    pub fn get_address(&self) -> &str {
        &self.address
    }

    // bytes value = 2;

    pub fn clear_value(&mut self) {
        self.value.clear();
    }

    // Param is passed by value, moved
    pub fn set_value(&mut self, v: ::std::vec::Vec<u8>) {
        self.value = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_value(&mut self) -> &mut ::std::vec::Vec<u8> {
        &mut self.value
    }

    // Take field
    pub fn take_value(&mut self) -> ::std::vec::Vec<u8> {
        ::std::mem::replace(&mut self.value, ::std::vec::Vec::new())
    }

    pub fn get_value(&self) -> &[u8] {
        &self.value
    }

    // .StateChange.Type type = 3;

    pub fn clear_field_type(&mut self) {
        self.field_type = StateChange_Type::TYPE_UNSET;
    }

    // Param is passed by value, moved
    pub fn set_field_type(&mut self, v: StateChange_Type) {
        self.field_type = v;
    }

    pub fn get_field_type(&self) -> StateChange_Type {
        self.field_type
    }
}

impl ::protobuf::Message for StateChange {
    fn is_initialized(&self) -> bool {
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_singular_proto3_string_into(wire_type, is, &mut self.address)?;
                },
                2 => {
                    ::protobuf::rt::read_singular_proto3_bytes_into(wire_type, is, &mut self.value)?;
                },
                3 => {
                    ::protobuf::rt::read_proto3_enum_with_unknown_fields_into(wire_type, is, &mut self.field_type, 3, &mut self.unknown_fields)?
                },
                _ => {
                    ::protobuf::rt::read_unknown_or_skip_group(field_number, wire_type, is, self.mut_unknown_fields())?;
                },
            };
        }
        ::std::result::Result::Ok(())
    }

    // Compute sizes of nested messages
    #[allow(unused_variables)]
    fn compute_size(&self) -> u32 {
        let mut my_size = 0;
        if !self.address.is_empty() {
            my_size += ::protobuf::rt::string_size(1, &self.address);
        }
        if !self.value.is_empty() {
            my_size += ::protobuf::rt::bytes_size(2, &self.value);
        }
        if self.field_type != StateChange_Type::TYPE_UNSET {
            my_size += ::protobuf::rt::enum_size(3, self.field_type);
        }
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        if !self.address.is_empty() {
            os.write_string(1, &self.address)?;
        }
        if !self.value.is_empty() {
            os.write_bytes(2, &self.value)?;
        }
        if self.field_type != StateChange_Type::TYPE_UNSET {
            os.write_enum(3, self.field_type.value())?;
        }
        os.write_unknown_fields(self.get_unknown_fields())?;
        ::std::result::Result::Ok(())
    }

    fn get_cached_size(&self) -> u32 {
        self.cached_size.get()
    }

    fn get_unknown_fields(&self) -> &::protobuf::UnknownFields {
        &self.unknown_fields
    }

    fn mut_unknown_fields(&mut self) -> &mut ::protobuf::UnknownFields {
        &mut self.unknown_fields
    }

    fn as_any(&self) -> &::std::any::Any {
        self as &::std::any::Any
    }
    fn as_any_mut(&mut self) -> &mut ::std::any::Any {
        self as &mut ::std::any::Any
    }
    fn into_any(self: Box<Self>) -> ::std::boxed::Box<::std::any::Any> {
        self
    }

    fn descriptor(&self) -> &'static ::protobuf::reflect::MessageDescriptor {
        Self::descriptor_static()
    }

    fn new() -> StateChange {
        StateChange::new()
    }

    fn descriptor_static() -> &'static ::protobuf::reflect::MessageDescriptor {
        static mut descriptor: ::protobuf::lazy::Lazy<::protobuf::reflect::MessageDescriptor> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ::protobuf::reflect::MessageDescriptor,
        };
        unsafe {
            descriptor.get(|| {
                let mut fields = ::std::vec::Vec::new();
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeString>(
                    "address",
                    |m: &StateChange| { &m.address },
                    |m: &mut StateChange| { &mut m.address },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeBytes>(
                    "value",
                    |m: &StateChange| { &m.value },
                    |m: &mut StateChange| { &mut m.value },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeEnum<StateChange_Type>>(
                    "type",
                    |m: &StateChange| { &m.field_type },
                    |m: &mut StateChange| { &mut m.field_type },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<StateChange>(
                    "StateChange",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static StateChange {
        static mut instance: ::protobuf::lazy::Lazy<StateChange> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const StateChange,
        };
        unsafe {
            instance.get(StateChange::new)
        }
    }
}

impl ::protobuf::Clear for StateChange {
    fn clear(&mut self) {
        self.clear_address();
        self.clear_value();
        self.clear_field_type();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for StateChange {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for StateChange {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

#[derive(Clone,PartialEq,Eq,Debug,Hash)]
pub enum StateChange_Type {
    TYPE_UNSET = 0,
    SET = 1,
    DELETE = 2,
}

impl ::protobuf::ProtobufEnum for StateChange_Type {
    fn value(&self) -> i32 {
        *self as i32
    }

    fn from_i32(value: i32) -> ::std::option::Option<StateChange_Type> {
        match value {
            0 => ::std::option::Option::Some(StateChange_Type::TYPE_UNSET),
            1 => ::std::option::Option::Some(StateChange_Type::SET),
            2 => ::std::option::Option::Some(StateChange_Type::DELETE),
            _ => ::std::option::Option::None
        }
    }

    fn values() -> &'static [Self] {
        static values: &'static [StateChange_Type] = &[
            StateChange_Type::TYPE_UNSET,
            StateChange_Type::SET,
            StateChange_Type::DELETE,
        ];
        values
    }

    fn enum_descriptor_static() -> &'static ::protobuf::reflect::EnumDescriptor {
        static mut descriptor: ::protobuf::lazy::Lazy<::protobuf::reflect::EnumDescriptor> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ::protobuf::reflect::EnumDescriptor,
        };
        unsafe {
            descriptor.get(|| {
                ::protobuf::reflect::EnumDescriptor::new("StateChange_Type", file_descriptor_proto())
            })
        }
    }
}

impl ::std::marker::Copy for StateChange_Type {
}

impl ::std::default::Default for StateChange_Type {
    fn default() -> Self {
        StateChange_Type::TYPE_UNSET
    }
}

impl ::protobuf::reflect::ProtobufValue for StateChange_Type {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Enum(self.descriptor())
    }
}

#[derive(PartialEq,Clone,Default)]
pub struct StateChangeList {
    // message fields
    pub state_changes: ::protobuf::RepeatedField<StateChange>,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl StateChangeList {
    pub fn new() -> StateChangeList {
        ::std::default::Default::default()
    }

    // repeated .StateChange state_changes = 1;

    pub fn clear_state_changes(&mut self) {
        self.state_changes.clear();
    }

    // Param is passed by value, moved
    pub fn set_state_changes(&mut self, v: ::protobuf::RepeatedField<StateChange>) {
        self.state_changes = v;
    }

    // Mutable pointer to the field.
    pub fn mut_state_changes(&mut self) -> &mut ::protobuf::RepeatedField<StateChange> {
        &mut self.state_changes
    }

    // Take field
    pub fn take_state_changes(&mut self) -> ::protobuf::RepeatedField<StateChange> {
        ::std::mem::replace(&mut self.state_changes, ::protobuf::RepeatedField::new())
    }

    pub fn get_state_changes(&self) -> &[StateChange] {
        &self.state_changes
    }
}

impl ::protobuf::Message for StateChangeList {
    fn is_initialized(&self) -> bool {
        for v in &self.state_changes {
            if !v.is_initialized() {
                return false;
            }
        };
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_repeated_message_into(wire_type, is, &mut self.state_changes)?;
                },
                _ => {
                    ::protobuf::rt::read_unknown_or_skip_group(field_number, wire_type, is, self.mut_unknown_fields())?;
                },
            };
        }
        ::std::result::Result::Ok(())
    }

    // Compute sizes of nested messages
    #[allow(unused_variables)]
    fn compute_size(&self) -> u32 {
        let mut my_size = 0;
        for value in &self.state_changes {
            let len = value.compute_size();
            my_size += 1 + ::protobuf::rt::compute_raw_varint32_size(len) + len;
        };
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        for v in &self.state_changes {
            os.write_tag(1, ::protobuf::wire_format::WireTypeLengthDelimited)?;
            os.write_raw_varint32(v.get_cached_size())?;
            v.write_to_with_cached_sizes(os)?;
        };
        os.write_unknown_fields(self.get_unknown_fields())?;
        ::std::result::Result::Ok(())
    }

    fn get_cached_size(&self) -> u32 {
        self.cached_size.get()
    }

    fn get_unknown_fields(&self) -> &::protobuf::UnknownFields {
        &self.unknown_fields
    }

    fn mut_unknown_fields(&mut self) -> &mut ::protobuf::UnknownFields {
        &mut self.unknown_fields
    }

    fn as_any(&self) -> &::std::any::Any {
        self as &::std::any::Any
    }
    fn as_any_mut(&mut self) -> &mut ::std::any::Any {
        self as &mut ::std::any::Any
    }
    fn into_any(self: Box<Self>) -> ::std::boxed::Box<::std::any::Any> {
        self
    }

    fn descriptor(&self) -> &'static ::protobuf::reflect::MessageDescriptor {
        Self::descriptor_static()
    }

    fn new() -> StateChangeList {
        StateChangeList::new()
    }

    fn descriptor_static() -> &'static ::protobuf::reflect::MessageDescriptor {
        static mut descriptor: ::protobuf::lazy::Lazy<::protobuf::reflect::MessageDescriptor> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ::protobuf::reflect::MessageDescriptor,
        };
        unsafe {
            descriptor.get(|| {
                let mut fields = ::std::vec::Vec::new();
                fields.push(::protobuf::reflect::accessor::make_repeated_field_accessor::<_, ::protobuf::types::ProtobufTypeMessage<StateChange>>(
                    "state_changes",
                    |m: &StateChangeList| { &m.state_changes },
                    |m: &mut StateChangeList| { &mut m.state_changes },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<StateChangeList>(
                    "StateChangeList",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static StateChangeList {
        static mut instance: ::protobuf::lazy::Lazy<StateChangeList> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const StateChangeList,
        };
        unsafe {
            instance.get(StateChangeList::new)
        }
    }
}

impl ::protobuf::Clear for StateChangeList {
    fn clear(&mut self) {
        self.clear_state_changes();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for StateChangeList {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for StateChangeList {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

static file_descriptor_proto_data: &'static [u8] = b"\
    \n\x19transaction_receipt.proto\x1a\x0cevents.proto\"\xa2\x01\n\x12Trans\
    actionReceipt\x121\n\rstate_changes\x18\x01\x20\x03(\x0b2\x0c.StateChang\
    eR\x0cstateChanges\x12\x1e\n\x06events\x18\x02\x20\x03(\x0b2\x06.EventR\
    \x06events\x12\x12\n\x04data\x18\x03\x20\x03(\x0cR\x04data\x12%\n\x0etra\
    nsaction_id\x18\x04\x20\x01(\tR\rtransactionId\"\x91\x01\n\x0bStateChang\
    e\x12\x18\n\x07address\x18\x01\x20\x01(\tR\x07address\x12\x14\n\x05value\
    \x18\x02\x20\x01(\x0cR\x05value\x12%\n\x04type\x18\x03\x20\x01(\x0e2\x11\
    .StateChange.TypeR\x04type\"+\n\x04Type\x12\x0e\n\nTYPE_UNSET\x10\0\x12\
    \x07\n\x03SET\x10\x01\x12\n\n\x06DELETE\x10\x02\"D\n\x0fStateChangeList\
    \x121\n\rstate_changes\x18\x01\x20\x03(\x0b2\x0c.StateChangeR\x0cstateCh\
    angesB*\n\x15sawtooth.sdk.protobufP\x01Z\x0ftxn_receipt_pb2b\x06proto3\
";

static mut file_descriptor_proto_lazy: ::protobuf::lazy::Lazy<::protobuf::descriptor::FileDescriptorProto> = ::protobuf::lazy::Lazy {
    lock: ::protobuf::lazy::ONCE_INIT,
    ptr: 0 as *const ::protobuf::descriptor::FileDescriptorProto,
};

fn parse_descriptor_proto() -> ::protobuf::descriptor::FileDescriptorProto {
    ::protobuf::parse_from_bytes(file_descriptor_proto_data).unwrap()
}

pub fn file_descriptor_proto() -> &'static ::protobuf::descriptor::FileDescriptorProto {
    unsafe {
        file_descriptor_proto_lazy.get(|| {
            parse_descriptor_proto()
        })
    }
}
