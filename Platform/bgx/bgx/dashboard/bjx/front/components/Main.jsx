import React from 'react'
import classNames from 'classnames/bind'

import Logo from './Logo'
import Graph from './Graph'
import Legend from './Legend'
import Transactions from './Transactions'
import Blocks from './Blocks'
import State from './State'

class Main extends React.Component {
  render() {
    return (
      <div className="container">
            <Logo/>
        <div className="row">
          <div className="col-4">
            <div className="row">
              <div className="col-12">
                <Graph/>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <Legend/>
              </div>
            </div>
          </div>
          <div className="col-8">
            <ul className={classNames('nav', 'nav-tabs')}>
              <li className="nav-item">
                <a className={classNames('nav-link active')}
                   id="transactions-tab"
                   data-toggle="tab"
                   href="#transactions"
                   role="tab">
                  Transactions
                </a>
              </li>
              <li className="nav-item">
                 <a className={classNames('nav-link ')}
                   id="blocks-tab"
                   data-toggle="tab"
                   href="#blocks"
                   role="tab">
                  Blocks
                </a>
              </li>
              <li className="nav-item">
                 <a className={classNames('nav-link ')}
                   id="state-tab"
                   data-toggle="tab"
                   href="#state"
                   role="tab">
                  State
                </a>
              </li>
            </ul>
            <div className="tab-content" id="btcontent">
              <Transactions className={classNames("tab-pane", "fade", "show", "active")} id="transactions" role="tabpanel"/>
              <Blocks className={classNames("tab-pane", "fade")} id="blocks" role="tabpanel"/>
              <State className={classNames("tab-pane", "fade")} id="state" role="tabpanel"/>
            </div>
          </div>
        </div>
      </div>);
  }
}

export default Main;
