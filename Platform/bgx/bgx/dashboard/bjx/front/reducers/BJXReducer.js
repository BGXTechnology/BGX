import { combineReducers } from 'redux'
import { GET_TRANSACTIONS, GET_PEERS, SELECT_PEER, GET_STATE, GET_BLOCKS } from '../actions/actions'

const initialState = {
  data: [],
}

const initialPeersState = {
  data: [],
  selectedPeer: null,
}

function stateReducer(state=initialState, action) {
  switch(action.type) {
    case GET_STATE:
      return Object.assign({}, state, {
        data: action.data
      });

      default:
        return state;
  }
  return state;
}

function blocksReducer(state=initialState, action) {
  switch(action.type) {
    case GET_BLOCKS:
      return Object.assign({}, state, {
        data: action.data
      });

      default:
        return state;
  }
  return state;
}

function transactionReducer(state=initialState, action) {
  switch(action.type) {
    case GET_TRANSACTIONS:
      return Object.assign({}, state, {
        data: action.data
      });

      default:
        return state;
  }
  return state;
}

function peersReducer(state=initialPeersState, action) {
  switch(action.type) {
    case GET_PEERS:
      return Object.assign({}, state, {
        data: action.data
      });

    case SELECT_PEER:
      return Object.assign({}, state, {
        selectedPeer: action.peer
      });

      default:
        return state;
  }
  return state;
}

const BJXReducer = combineReducers({
  transactionReducer,
  peersReducer,
  stateReducer,
  blocksReducer,
})

export default BJXReducer;
