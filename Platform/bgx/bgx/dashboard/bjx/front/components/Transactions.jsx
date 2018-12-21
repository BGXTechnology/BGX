import React from 'react'
import { connect } from 'react-redux'
import classNames from 'classnames/bind'

import Hash from './Hash'

import ReactTable from 'react-table'

class Transactions extends React.Component {
  render() {
    const {transactions, columns, className, id, role} = this.props
    return (
      <div className={className} id={id} role={role}>
      {!transactions.length ? (
      <strong> No transactions</strong>
      ) : (
      <ReactTable data={transactions}
        defaultPageSize={10}
        minRows={0}
        columns={columns}
        className='-striped'/>
        )}
    </div>
    )
  }
}

Transactions.defaultProps = {
  transactions: [],
  columns: [
  {
    id: 'family',
    Header: 'Family name (family version)',
    accessor: t => `${t.header.family_name} ${t.header.family_version}`,
  },
  { id: 'inputs',
    Header: 'Inputs',
    accessor: t => t.header.inputs.map((i) => {
          return (  <Hash key={i} hash={i}/> )
        })
  },
  {
    id: 'outputs',
    Header: 'Outputs',
    accessor: t => t.header.outputs.map((i) => {
          return (  <Hash key={i} hash={i}/> )
        })
  },
  { id: 'from',
    Header: 'From',
    accessor: d => <i>wallet key</i>,
  },
  { id: 'to',
    Header: 'To',
    accessor: d => <i>wallet key</i>,
  },
  { id: 'signerPublicKey',
    Header: 'Signer Public Key',
    accessor: d => <Hash hash={d.header.signer_public_key}/>,
  }]
};

function mapStateToProps(store) {
  return {
    transactions: store.transactionReducer.data,
  };
}

export default connect (mapStateToProps, null)(Transactions);
