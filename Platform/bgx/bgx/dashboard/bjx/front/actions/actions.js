import axios from 'axios';

import { nodes, transactions, state, blocks } from '../dummies'

import { convertPeers } from '../logic/peers'
import { convertTransactions } from '../logic/transactions'
import { convertState } from '../logic/state'
import { convertBlocks } from '../logic/blocks'

const apiUrl = 'http://18.222.233.160:8003';

export const GET_TRANSACTIONS = 'GET_TRANSACTIONS';

export const GET_STATE = 'GET_STATE';

export const GET_BLOCKS = 'GET_BLOCKS';

export const GET_PEERS = 'GET_PEERS';
export const SELECT_PEER = 'SELECT_PEER';

export function getTransactions() {
    //TEMP
  //return getTransactionsSuccess(convertTransactions(transactions));
  //END TEMP

  return function(dispatch) {
    return axios.get(`${apiUrl}/transactions`)
      .then( response => {
        dispatch(getTransactionsSuccess(convertTransactions(response.data)))
      })
      .catch(error => {
        //throw(error);
        console.log(error)
        dispatch(getTransactionsSuccess(convertTransactions(transactions)));
      })
  };
}

export function getState() {
    //TEMP
  //return getStateSuccess(convertState(state));
  //END TEMP

  return function(dispatch) {
    return axios.get(`${apiUrl}/state`)
      .then( response => {
        dispatch(getStateSuccess(convertState(response.data)))
      })
      .catch(error => {
        //throw(error);
        console.log(error)
        dispatch(getStateSuccess(convertState(state)));
      })
  };
}

export function getBlocks() {
  //TEMP
  //return getBlocksSuccess(convertBlocks(blocks));
  //END TEMP

  return function(dispatch) {
    return axios.get(`${apiUrl}/blocks`)
      .then( response => {
        dispatch(getBlocksSuccess(convertBlocks(response.data)))
      })
      .catch(error => {
        // throw(error);
        console.log(error)
        dispatch(getBlocksSuccess(convertBlocks(blocks)));
      })
  };
}

export function getPeers() {
  //TEMP
  //return getPeersSuccess(convertPeers(nodes));
  //END TEMP

  return function(dispatch) {
    return axios.get(`${apiUrl}/peers`)
      .then( response => {
        dispatch(getPeersSuccess(convertPeers(response.data)))
      })
      .catch(error => {
        // throw(error);
        console.log(error)
        dispatch(getPeersSuccess(convertPeers(nodes)))
      })
  };
}

export function selectPeer(peer) {
  return {
    type: SELECT_PEER,
    peer,
    };
}

function getStateSuccess(data) {
  return {
    type: GET_STATE,
    data,
    };
}

function getBlocksSuccess(data) {
  return {
    type: GET_BLOCKS,
    data,
    };
}

function getPeersSuccess(data) {
  return {
    type: GET_PEERS,
    data,
    };
}

function getTransactionsSuccess(data) {
  return {
    type: GET_TRANSACTIONS,
    data,
    };
}
