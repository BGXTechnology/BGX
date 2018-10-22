# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: sawtooth_rest_api/protobuf/client_batch_submit.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from sawtooth_rest_api.protobuf import batch_pb2 as sawtooth__rest__api_dot_protobuf_dot_batch__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='sawtooth_rest_api/protobuf/client_batch_submit.proto',
  package='',
  syntax='proto3',
  serialized_pb=_b('\n4sawtooth_rest_api/protobuf/client_batch_submit.proto\x1a&sawtooth_rest_api/protobuf/batch.proto\"\xbd\x02\n\x11\x43lientBatchStatus\x12\x10\n\x08\x62\x61tch_id\x18\x01 \x01(\t\x12)\n\x06status\x18\x02 \x01(\x0e\x32\x19.ClientBatchStatus.Status\x12\x43\n\x14invalid_transactions\x18\x03 \x03(\x0b\x32%.ClientBatchStatus.InvalidTransaction\x1aT\n\x12InvalidTransaction\x12\x16\n\x0etransaction_id\x18\x01 \x01(\t\x12\x0f\n\x07message\x18\x02 \x01(\t\x12\x15\n\rextended_data\x18\x03 \x01(\x0c\"P\n\x06Status\x12\x10\n\x0cSTATUS_UNSET\x10\x00\x12\r\n\tCOMMITTED\x10\x01\x12\x0b\n\x07INVALID\x10\x02\x12\x0b\n\x07PENDING\x10\x03\x12\x0b\n\x07UNKNOWN\x10\x04\"3\n\x18\x43lientBatchSubmitRequest\x12\x17\n\x07\x62\x61tches\x18\x01 \x03(\x0b\x32\x06.Batch\"\xa9\x01\n\x19\x43lientBatchSubmitResponse\x12\x31\n\x06status\x18\x01 \x01(\x0e\x32!.ClientBatchSubmitResponse.Status\"Y\n\x06Status\x12\x10\n\x0cSTATUS_UNSET\x10\x00\x12\x06\n\x02OK\x10\x01\x12\x12\n\x0eINTERNAL_ERROR\x10\x02\x12\x11\n\rINVALID_BATCH\x10\x03\x12\x0e\n\nQUEUE_FULL\x10\x04\"L\n\x18\x43lientBatchStatusRequest\x12\x11\n\tbatch_ids\x18\x01 \x03(\t\x12\x0c\n\x04wait\x18\x02 \x01(\x08\x12\x0f\n\x07timeout\x18\x03 \x01(\r\"\xd3\x01\n\x19\x43lientBatchStatusResponse\x12\x31\n\x06status\x18\x01 \x01(\x0e\x32!.ClientBatchStatusResponse.Status\x12*\n\x0e\x62\x61tch_statuses\x18\x02 \x03(\x0b\x32\x12.ClientBatchStatus\"W\n\x06Status\x12\x10\n\x0cSTATUS_UNSET\x10\x00\x12\x06\n\x02OK\x10\x01\x12\x12\n\x0eINTERNAL_ERROR\x10\x02\x12\x0f\n\x0bNO_RESOURCE\x10\x05\x12\x0e\n\nINVALID_ID\x10\x08\x42\x32\n\x15sawtooth.sdk.protobufP\x01Z\x17\x63lient_batch_submit_pb2b\x06proto3')
  ,
  dependencies=[sawtooth__rest__api_dot_protobuf_dot_batch__pb2.DESCRIPTOR,])
_sym_db.RegisterFileDescriptor(DESCRIPTOR)



_CLIENTBATCHSTATUS_STATUS = _descriptor.EnumDescriptor(
  name='Status',
  full_name='ClientBatchStatus.Status',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='STATUS_UNSET', index=0, number=0,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='COMMITTED', index=1, number=1,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='INVALID', index=2, number=2,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='PENDING', index=3, number=3,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='UNKNOWN', index=4, number=4,
      options=None,
      type=None),
  ],
  containing_type=None,
  options=None,
  serialized_start=334,
  serialized_end=414,
)
_sym_db.RegisterEnumDescriptor(_CLIENTBATCHSTATUS_STATUS)

_CLIENTBATCHSUBMITRESPONSE_STATUS = _descriptor.EnumDescriptor(
  name='Status',
  full_name='ClientBatchSubmitResponse.Status',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='STATUS_UNSET', index=0, number=0,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='OK', index=1, number=1,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='INTERNAL_ERROR', index=2, number=2,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='INVALID_BATCH', index=3, number=3,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='QUEUE_FULL', index=4, number=4,
      options=None,
      type=None),
  ],
  containing_type=None,
  options=None,
  serialized_start=550,
  serialized_end=639,
)
_sym_db.RegisterEnumDescriptor(_CLIENTBATCHSUBMITRESPONSE_STATUS)

_CLIENTBATCHSTATUSRESPONSE_STATUS = _descriptor.EnumDescriptor(
  name='Status',
  full_name='ClientBatchStatusResponse.Status',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='STATUS_UNSET', index=0, number=0,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='OK', index=1, number=1,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='INTERNAL_ERROR', index=2, number=2,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='NO_RESOURCE', index=3, number=5,
      options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='INVALID_ID', index=4, number=8,
      options=None,
      type=None),
  ],
  containing_type=None,
  options=None,
  serialized_start=844,
  serialized_end=931,
)
_sym_db.RegisterEnumDescriptor(_CLIENTBATCHSTATUSRESPONSE_STATUS)


_CLIENTBATCHSTATUS_INVALIDTRANSACTION = _descriptor.Descriptor(
  name='InvalidTransaction',
  full_name='ClientBatchStatus.InvalidTransaction',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='transaction_id', full_name='ClientBatchStatus.InvalidTransaction.transaction_id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='message', full_name='ClientBatchStatus.InvalidTransaction.message', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='extended_data', full_name='ClientBatchStatus.InvalidTransaction.extended_data', index=2,
      number=3, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=248,
  serialized_end=332,
)

_CLIENTBATCHSTATUS = _descriptor.Descriptor(
  name='ClientBatchStatus',
  full_name='ClientBatchStatus',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='batch_id', full_name='ClientBatchStatus.batch_id', index=0,
      number=1, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=_b("").decode('utf-8'),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='status', full_name='ClientBatchStatus.status', index=1,
      number=2, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='invalid_transactions', full_name='ClientBatchStatus.invalid_transactions', index=2,
      number=3, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[_CLIENTBATCHSTATUS_INVALIDTRANSACTION, ],
  enum_types=[
    _CLIENTBATCHSTATUS_STATUS,
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=97,
  serialized_end=414,
)


_CLIENTBATCHSUBMITREQUEST = _descriptor.Descriptor(
  name='ClientBatchSubmitRequest',
  full_name='ClientBatchSubmitRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='batches', full_name='ClientBatchSubmitRequest.batches', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=416,
  serialized_end=467,
)


_CLIENTBATCHSUBMITRESPONSE = _descriptor.Descriptor(
  name='ClientBatchSubmitResponse',
  full_name='ClientBatchSubmitResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='status', full_name='ClientBatchSubmitResponse.status', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
    _CLIENTBATCHSUBMITRESPONSE_STATUS,
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=470,
  serialized_end=639,
)


_CLIENTBATCHSTATUSREQUEST = _descriptor.Descriptor(
  name='ClientBatchStatusRequest',
  full_name='ClientBatchStatusRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='batch_ids', full_name='ClientBatchStatusRequest.batch_ids', index=0,
      number=1, type=9, cpp_type=9, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='wait', full_name='ClientBatchStatusRequest.wait', index=1,
      number=2, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='timeout', full_name='ClientBatchStatusRequest.timeout', index=2,
      number=3, type=13, cpp_type=3, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=641,
  serialized_end=717,
)


_CLIENTBATCHSTATUSRESPONSE = _descriptor.Descriptor(
  name='ClientBatchStatusResponse',
  full_name='ClientBatchStatusResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='status', full_name='ClientBatchStatusResponse.status', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='batch_statuses', full_name='ClientBatchStatusResponse.batch_statuses', index=1,
      number=2, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
    _CLIENTBATCHSTATUSRESPONSE_STATUS,
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=720,
  serialized_end=931,
)

_CLIENTBATCHSTATUS_INVALIDTRANSACTION.containing_type = _CLIENTBATCHSTATUS
_CLIENTBATCHSTATUS.fields_by_name['status'].enum_type = _CLIENTBATCHSTATUS_STATUS
_CLIENTBATCHSTATUS.fields_by_name['invalid_transactions'].message_type = _CLIENTBATCHSTATUS_INVALIDTRANSACTION
_CLIENTBATCHSTATUS_STATUS.containing_type = _CLIENTBATCHSTATUS
_CLIENTBATCHSUBMITREQUEST.fields_by_name['batches'].message_type = sawtooth__rest__api_dot_protobuf_dot_batch__pb2._BATCH
_CLIENTBATCHSUBMITRESPONSE.fields_by_name['status'].enum_type = _CLIENTBATCHSUBMITRESPONSE_STATUS
_CLIENTBATCHSUBMITRESPONSE_STATUS.containing_type = _CLIENTBATCHSUBMITRESPONSE
_CLIENTBATCHSTATUSRESPONSE.fields_by_name['status'].enum_type = _CLIENTBATCHSTATUSRESPONSE_STATUS
_CLIENTBATCHSTATUSRESPONSE.fields_by_name['batch_statuses'].message_type = _CLIENTBATCHSTATUS
_CLIENTBATCHSTATUSRESPONSE_STATUS.containing_type = _CLIENTBATCHSTATUSRESPONSE
DESCRIPTOR.message_types_by_name['ClientBatchStatus'] = _CLIENTBATCHSTATUS
DESCRIPTOR.message_types_by_name['ClientBatchSubmitRequest'] = _CLIENTBATCHSUBMITREQUEST
DESCRIPTOR.message_types_by_name['ClientBatchSubmitResponse'] = _CLIENTBATCHSUBMITRESPONSE
DESCRIPTOR.message_types_by_name['ClientBatchStatusRequest'] = _CLIENTBATCHSTATUSREQUEST
DESCRIPTOR.message_types_by_name['ClientBatchStatusResponse'] = _CLIENTBATCHSTATUSRESPONSE

ClientBatchStatus = _reflection.GeneratedProtocolMessageType('ClientBatchStatus', (_message.Message,), dict(

  InvalidTransaction = _reflection.GeneratedProtocolMessageType('InvalidTransaction', (_message.Message,), dict(
    DESCRIPTOR = _CLIENTBATCHSTATUS_INVALIDTRANSACTION,
    __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
    # @@protoc_insertion_point(class_scope:ClientBatchStatus.InvalidTransaction)
    ))
  ,
  DESCRIPTOR = _CLIENTBATCHSTATUS,
  __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
  # @@protoc_insertion_point(class_scope:ClientBatchStatus)
  ))
_sym_db.RegisterMessage(ClientBatchStatus)
_sym_db.RegisterMessage(ClientBatchStatus.InvalidTransaction)

ClientBatchSubmitRequest = _reflection.GeneratedProtocolMessageType('ClientBatchSubmitRequest', (_message.Message,), dict(
  DESCRIPTOR = _CLIENTBATCHSUBMITREQUEST,
  __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
  # @@protoc_insertion_point(class_scope:ClientBatchSubmitRequest)
  ))
_sym_db.RegisterMessage(ClientBatchSubmitRequest)

ClientBatchSubmitResponse = _reflection.GeneratedProtocolMessageType('ClientBatchSubmitResponse', (_message.Message,), dict(
  DESCRIPTOR = _CLIENTBATCHSUBMITRESPONSE,
  __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
  # @@protoc_insertion_point(class_scope:ClientBatchSubmitResponse)
  ))
_sym_db.RegisterMessage(ClientBatchSubmitResponse)

ClientBatchStatusRequest = _reflection.GeneratedProtocolMessageType('ClientBatchStatusRequest', (_message.Message,), dict(
  DESCRIPTOR = _CLIENTBATCHSTATUSREQUEST,
  __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
  # @@protoc_insertion_point(class_scope:ClientBatchStatusRequest)
  ))
_sym_db.RegisterMessage(ClientBatchStatusRequest)

ClientBatchStatusResponse = _reflection.GeneratedProtocolMessageType('ClientBatchStatusResponse', (_message.Message,), dict(
  DESCRIPTOR = _CLIENTBATCHSTATUSRESPONSE,
  __module__ = 'sawtooth_rest_api.protobuf.client_batch_submit_pb2'
  # @@protoc_insertion_point(class_scope:ClientBatchStatusResponse)
  ))
_sym_db.RegisterMessage(ClientBatchStatusResponse)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('\n\025sawtooth.sdk.protobufP\001Z\027client_batch_submit_pb2'))
# @@protoc_insertion_point(module_scope)
