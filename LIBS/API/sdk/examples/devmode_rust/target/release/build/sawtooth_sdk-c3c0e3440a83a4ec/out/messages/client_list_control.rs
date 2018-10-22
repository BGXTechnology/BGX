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
pub struct ClientPagingControls {
    // message fields
    pub start: ::std::string::String,
    pub limit: i32,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl ClientPagingControls {
    pub fn new() -> ClientPagingControls {
        ::std::default::Default::default()
    }

    // string start = 1;

    pub fn clear_start(&mut self) {
        self.start.clear();
    }

    // Param is passed by value, moved
    pub fn set_start(&mut self, v: ::std::string::String) {
        self.start = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_start(&mut self) -> &mut ::std::string::String {
        &mut self.start
    }

    // Take field
    pub fn take_start(&mut self) -> ::std::string::String {
        ::std::mem::replace(&mut self.start, ::std::string::String::new())
    }

    pub fn get_start(&self) -> &str {
        &self.start
    }

    // int32 limit = 2;

    pub fn clear_limit(&mut self) {
        self.limit = 0;
    }

    // Param is passed by value, moved
    pub fn set_limit(&mut self, v: i32) {
        self.limit = v;
    }

    pub fn get_limit(&self) -> i32 {
        self.limit
    }
}

impl ::protobuf::Message for ClientPagingControls {
    fn is_initialized(&self) -> bool {
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_singular_proto3_string_into(wire_type, is, &mut self.start)?;
                },
                2 => {
                    if wire_type != ::protobuf::wire_format::WireTypeVarint {
                        return ::std::result::Result::Err(::protobuf::rt::unexpected_wire_type(wire_type));
                    }
                    let tmp = is.read_int32()?;
                    self.limit = tmp;
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
        if !self.start.is_empty() {
            my_size += ::protobuf::rt::string_size(1, &self.start);
        }
        if self.limit != 0 {
            my_size += ::protobuf::rt::value_size(2, self.limit, ::protobuf::wire_format::WireTypeVarint);
        }
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        if !self.start.is_empty() {
            os.write_string(1, &self.start)?;
        }
        if self.limit != 0 {
            os.write_int32(2, self.limit)?;
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

    fn new() -> ClientPagingControls {
        ClientPagingControls::new()
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
                    "start",
                    |m: &ClientPagingControls| { &m.start },
                    |m: &mut ClientPagingControls| { &mut m.start },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeInt32>(
                    "limit",
                    |m: &ClientPagingControls| { &m.limit },
                    |m: &mut ClientPagingControls| { &mut m.limit },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<ClientPagingControls>(
                    "ClientPagingControls",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static ClientPagingControls {
        static mut instance: ::protobuf::lazy::Lazy<ClientPagingControls> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ClientPagingControls,
        };
        unsafe {
            instance.get(ClientPagingControls::new)
        }
    }
}

impl ::protobuf::Clear for ClientPagingControls {
    fn clear(&mut self) {
        self.clear_start();
        self.clear_limit();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for ClientPagingControls {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for ClientPagingControls {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

#[derive(PartialEq,Clone,Default)]
pub struct ClientPagingResponse {
    // message fields
    pub next: ::std::string::String,
    pub start: ::std::string::String,
    pub limit: i32,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl ClientPagingResponse {
    pub fn new() -> ClientPagingResponse {
        ::std::default::Default::default()
    }

    // string next = 1;

    pub fn clear_next(&mut self) {
        self.next.clear();
    }

    // Param is passed by value, moved
    pub fn set_next(&mut self, v: ::std::string::String) {
        self.next = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_next(&mut self) -> &mut ::std::string::String {
        &mut self.next
    }

    // Take field
    pub fn take_next(&mut self) -> ::std::string::String {
        ::std::mem::replace(&mut self.next, ::std::string::String::new())
    }

    pub fn get_next(&self) -> &str {
        &self.next
    }

    // string start = 2;

    pub fn clear_start(&mut self) {
        self.start.clear();
    }

    // Param is passed by value, moved
    pub fn set_start(&mut self, v: ::std::string::String) {
        self.start = v;
    }

    // Mutable pointer to the field.
    // If field is not initialized, it is initialized with default value first.
    pub fn mut_start(&mut self) -> &mut ::std::string::String {
        &mut self.start
    }

    // Take field
    pub fn take_start(&mut self) -> ::std::string::String {
        ::std::mem::replace(&mut self.start, ::std::string::String::new())
    }

    pub fn get_start(&self) -> &str {
        &self.start
    }

    // int32 limit = 3;

    pub fn clear_limit(&mut self) {
        self.limit = 0;
    }

    // Param is passed by value, moved
    pub fn set_limit(&mut self, v: i32) {
        self.limit = v;
    }

    pub fn get_limit(&self) -> i32 {
        self.limit
    }
}

impl ::protobuf::Message for ClientPagingResponse {
    fn is_initialized(&self) -> bool {
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_singular_proto3_string_into(wire_type, is, &mut self.next)?;
                },
                2 => {
                    ::protobuf::rt::read_singular_proto3_string_into(wire_type, is, &mut self.start)?;
                },
                3 => {
                    if wire_type != ::protobuf::wire_format::WireTypeVarint {
                        return ::std::result::Result::Err(::protobuf::rt::unexpected_wire_type(wire_type));
                    }
                    let tmp = is.read_int32()?;
                    self.limit = tmp;
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
        if !self.next.is_empty() {
            my_size += ::protobuf::rt::string_size(1, &self.next);
        }
        if !self.start.is_empty() {
            my_size += ::protobuf::rt::string_size(2, &self.start);
        }
        if self.limit != 0 {
            my_size += ::protobuf::rt::value_size(3, self.limit, ::protobuf::wire_format::WireTypeVarint);
        }
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        if !self.next.is_empty() {
            os.write_string(1, &self.next)?;
        }
        if !self.start.is_empty() {
            os.write_string(2, &self.start)?;
        }
        if self.limit != 0 {
            os.write_int32(3, self.limit)?;
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

    fn new() -> ClientPagingResponse {
        ClientPagingResponse::new()
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
                    "next",
                    |m: &ClientPagingResponse| { &m.next },
                    |m: &mut ClientPagingResponse| { &mut m.next },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeString>(
                    "start",
                    |m: &ClientPagingResponse| { &m.start },
                    |m: &mut ClientPagingResponse| { &mut m.start },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeInt32>(
                    "limit",
                    |m: &ClientPagingResponse| { &m.limit },
                    |m: &mut ClientPagingResponse| { &mut m.limit },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<ClientPagingResponse>(
                    "ClientPagingResponse",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static ClientPagingResponse {
        static mut instance: ::protobuf::lazy::Lazy<ClientPagingResponse> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ClientPagingResponse,
        };
        unsafe {
            instance.get(ClientPagingResponse::new)
        }
    }
}

impl ::protobuf::Clear for ClientPagingResponse {
    fn clear(&mut self) {
        self.clear_next();
        self.clear_start();
        self.clear_limit();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for ClientPagingResponse {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for ClientPagingResponse {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

#[derive(PartialEq,Clone,Default)]
pub struct ClientSortControls {
    // message fields
    pub keys: ::protobuf::RepeatedField<::std::string::String>,
    pub reverse: bool,
    // special fields
    unknown_fields: ::protobuf::UnknownFields,
    cached_size: ::protobuf::CachedSize,
}

impl ClientSortControls {
    pub fn new() -> ClientSortControls {
        ::std::default::Default::default()
    }

    // repeated string keys = 1;

    pub fn clear_keys(&mut self) {
        self.keys.clear();
    }

    // Param is passed by value, moved
    pub fn set_keys(&mut self, v: ::protobuf::RepeatedField<::std::string::String>) {
        self.keys = v;
    }

    // Mutable pointer to the field.
    pub fn mut_keys(&mut self) -> &mut ::protobuf::RepeatedField<::std::string::String> {
        &mut self.keys
    }

    // Take field
    pub fn take_keys(&mut self) -> ::protobuf::RepeatedField<::std::string::String> {
        ::std::mem::replace(&mut self.keys, ::protobuf::RepeatedField::new())
    }

    pub fn get_keys(&self) -> &[::std::string::String] {
        &self.keys
    }

    // bool reverse = 2;

    pub fn clear_reverse(&mut self) {
        self.reverse = false;
    }

    // Param is passed by value, moved
    pub fn set_reverse(&mut self, v: bool) {
        self.reverse = v;
    }

    pub fn get_reverse(&self) -> bool {
        self.reverse
    }
}

impl ::protobuf::Message for ClientSortControls {
    fn is_initialized(&self) -> bool {
        true
    }

    fn merge_from(&mut self, is: &mut ::protobuf::CodedInputStream) -> ::protobuf::ProtobufResult<()> {
        while !is.eof()? {
            let (field_number, wire_type) = is.read_tag_unpack()?;
            match field_number {
                1 => {
                    ::protobuf::rt::read_repeated_string_into(wire_type, is, &mut self.keys)?;
                },
                2 => {
                    if wire_type != ::protobuf::wire_format::WireTypeVarint {
                        return ::std::result::Result::Err(::protobuf::rt::unexpected_wire_type(wire_type));
                    }
                    let tmp = is.read_bool()?;
                    self.reverse = tmp;
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
        for value in &self.keys {
            my_size += ::protobuf::rt::string_size(1, &value);
        };
        if self.reverse != false {
            my_size += 2;
        }
        my_size += ::protobuf::rt::unknown_fields_size(self.get_unknown_fields());
        self.cached_size.set(my_size);
        my_size
    }

    fn write_to_with_cached_sizes(&self, os: &mut ::protobuf::CodedOutputStream) -> ::protobuf::ProtobufResult<()> {
        for v in &self.keys {
            os.write_string(1, &v)?;
        };
        if self.reverse != false {
            os.write_bool(2, self.reverse)?;
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

    fn new() -> ClientSortControls {
        ClientSortControls::new()
    }

    fn descriptor_static() -> &'static ::protobuf::reflect::MessageDescriptor {
        static mut descriptor: ::protobuf::lazy::Lazy<::protobuf::reflect::MessageDescriptor> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ::protobuf::reflect::MessageDescriptor,
        };
        unsafe {
            descriptor.get(|| {
                let mut fields = ::std::vec::Vec::new();
                fields.push(::protobuf::reflect::accessor::make_repeated_field_accessor::<_, ::protobuf::types::ProtobufTypeString>(
                    "keys",
                    |m: &ClientSortControls| { &m.keys },
                    |m: &mut ClientSortControls| { &mut m.keys },
                ));
                fields.push(::protobuf::reflect::accessor::make_simple_field_accessor::<_, ::protobuf::types::ProtobufTypeBool>(
                    "reverse",
                    |m: &ClientSortControls| { &m.reverse },
                    |m: &mut ClientSortControls| { &mut m.reverse },
                ));
                ::protobuf::reflect::MessageDescriptor::new::<ClientSortControls>(
                    "ClientSortControls",
                    fields,
                    file_descriptor_proto()
                )
            })
        }
    }

    fn default_instance() -> &'static ClientSortControls {
        static mut instance: ::protobuf::lazy::Lazy<ClientSortControls> = ::protobuf::lazy::Lazy {
            lock: ::protobuf::lazy::ONCE_INIT,
            ptr: 0 as *const ClientSortControls,
        };
        unsafe {
            instance.get(ClientSortControls::new)
        }
    }
}

impl ::protobuf::Clear for ClientSortControls {
    fn clear(&mut self) {
        self.clear_keys();
        self.clear_reverse();
        self.unknown_fields.clear();
    }
}

impl ::std::fmt::Debug for ClientSortControls {
    fn fmt(&self, f: &mut ::std::fmt::Formatter) -> ::std::fmt::Result {
        ::protobuf::text_format::fmt(self, f)
    }
}

impl ::protobuf::reflect::ProtobufValue for ClientSortControls {
    fn as_ref(&self) -> ::protobuf::reflect::ProtobufValueRef {
        ::protobuf::reflect::ProtobufValueRef::Message(self)
    }
}

static file_descriptor_proto_data: &'static [u8] = b"\
    \n\x19client_list_control.proto\"B\n\x14ClientPagingControls\x12\x14\n\
    \x05start\x18\x01\x20\x01(\tR\x05start\x12\x14\n\x05limit\x18\x02\x20\
    \x01(\x05R\x05limit\"V\n\x14ClientPagingResponse\x12\x12\n\x04next\x18\
    \x01\x20\x01(\tR\x04next\x12\x14\n\x05start\x18\x02\x20\x01(\tR\x05start\
    \x12\x14\n\x05limit\x18\x03\x20\x01(\x05R\x05limit\"B\n\x12ClientSortCon\
    trols\x12\x12\n\x04keys\x18\x01\x20\x03(\tR\x04keys\x12\x18\n\x07reverse\
    \x18\x02\x20\x01(\x08R\x07reverseB2\n\x15sawtooth.sdk.protobufP\x01Z\x17\
    client_list_control_pb2b\x06proto3\
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
