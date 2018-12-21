import React from 'react'
import { connect } from 'react-redux'
import classNames from 'classnames/bind'
import Hash from './Hash'

import ReactTable from "react-table"

class State extends React.Component {
  render() {
    const {state, columns, className, id, role} = this.props;
    return (<div className={className} id={id} role={role}>
      {!state.length  ? (
        <strong> No State</strong>
      ) : (
        <ReactTable data={state}
          defaultPageSize={10}
          columns={columns}
          minRows={0}
          className='-striped'/>
      )}
      </div>
    )
  }
}

State.defaultProps = {
  state: [],
  columns: [{
    id: 'address',
    Header: 'Address',
    accessor: t => <Hash hash={t.address}/>,
  },{
    id: 'data',
    Header: 'Data',
    accessor: t => <Hash hash={t.data}/>,
  },]
};

function mapStateToProps(store) {
  return {
    state: store.stateReducer.data,
  };
}

export default connect (mapStateToProps, null)(State);
