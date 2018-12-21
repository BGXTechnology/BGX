import React from 'react'
import LogoSvg from '../assets/logo.svg'
import classNames from 'classnames/bind'

import { getTransactions, getPeers, getState, getBlocks } from '../actions/actions';

class Logo extends React.Component {
  constructor(props){
    super(props)

    this.handleClick = this.handleClick.bind(this);
  }
  handleClick() {
    store.dispatch(getTransactions());
    store.dispatch(getPeers());
    store.dispatch(getState());
    store.dispatch(getBlocks());
  }
  render() {
    return (
      <nav className={classNames('navbar', 'navbar-light', 'bg-light')}>

        <a className="navbar-brand" href="#">
         <span className="logo" dangerouslySetInnerHTML={{ __html: LogoSvg }}></span>
        </a>
        BGX Web viewer [ALPHA]
        <a to="#" onClick={this.handleClick} className="btn btn-outline-success">Update</a>
      </nav>);
  }
}

export default Logo;
