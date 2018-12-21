import React from 'react'
import { connect } from 'react-redux'

class Legend extends React.Component {
  render() {
    const {peer} = this.props;

    if (peer == null)
      return (
      <div className="card">
        <div className="card-body">
          <h5 className="card-title">Legend</h5>
          <b>No data</b>
        </div>
      </div>);
    else
      return (
        <div className="card">
          <div className="card-body">
          <h5 className="card-title">Legend</h5>
          Public Key: {peer.public_key}
          <br/>
                    Ip: {peer.IP}

          <br/>
                    Port: {peer.port}
          <br/>
                    node_state: {peer.node_state}
          <br/>
                    node_type: {peer.node_type}
          <br/>
          Age: <i className='text-muted'> 4months 3 days 4 hours</i>
          <br/>
          Register Date: <i className='text-muted'> 28 June 2008</i>
          <br/>
          Clusters: <i className='text-muted'> TacoBell AirPlans</i>
                  <br/>
          Balance (DEC): <i className='text-muted'> 120.156</i>
                  <br/>
          Mined: <i className='text-muted'> 300 blocks</i>
          <br/>
          <br/>
          BGT name: <i className='text-muted'>Tacos</i>
          <br/>
          Balance (BGT):
          <br/>
           <i className='text-muted'> 120.156 Ice creams</i>
           <br/>
            <i className='text-muted'> 120.156 Tacos</i>
            <br/>
             <i className='text-muted'> 120.156 Miles</i>
          <br/>
          <br/>
                  Fee (%):
           <i className='text-muted'> 1.15</i>
          <br/>
          Transaction summary fee
          <br/>
           <i className='text-muted'> 120.156 Ice creams</i>
           <br/>
            <i className='text-muted'> 120.156 Tacos</i>
            <br/>
             <i className='text-muted'> 120.156 Miles</i>
          <br/>
          </div>
        </div>);
  }
}
function mapStateToProps(store) {
  return {
    peer: store.peersReducer.selectedPeer,
  };
}

export default connect (mapStateToProps, null)(Legend);
