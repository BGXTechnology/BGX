import React from 'react';
import { render } from 'react-dom';

import { createStore, applyMiddleware, compose } from 'redux';
import { Provider } from 'react-redux'
import thunk from 'redux-thunk'

import 'bootstrap';
import './app.scss';

import Main from './components/Main';

import BJXReducer from './reducers/BJXReducer';

import { getTransactions, getPeers, getState, getBlocks } from './actions/actions';

const composeEnhancer = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

window.store = createStore(BJXReducer,
  composeEnhancer(applyMiddleware(thunk)),);

store.dispatch(getTransactions());
store.dispatch(getPeers());
store.dispatch(getState());
store.dispatch(getBlocks());

render(
  <Provider store={store}>
    <Main/>
  </Provider>
  , document.getElementById("app")
);
