import React from 'react'
import { connect } from 'react-redux'
import classNames from 'classnames/bind'

import Hash from './Hash'

import ReactTable from "react-table"

class Blocks extends React.Component {
  constructor(props){
    super(props)
    this.state= { selectedBlockNum: null}
  }

  componentDidUpdate() {
    const data = Object.assign({}, this.props.graph_blocks);

    const { selectedBlockNum } = this.state;
    const that = this;

    var width = 680,
        height = 600,
        root;

    var force = d3.layout.force()
        .linkDistance(50)
        .charge(-120)
        .gravity(.05)
        .size([width, height])
        .on("tick", tick);

    var svg = d3.select(".chart-block")
        .attr("width", width)
        .attr("height", height);

    var link = svg.selectAll(".link"),
        node = svg.selectAll(".node");

    // d3.json("graph.json", function(error, json) {
    //   if (error) throw error;

      root = data;
      update();
    // });

    function update() {
      var nodes = flatten(root),
          links = d3.layout.tree().links(nodes);

      // Restart the force layout.
      force
          .nodes(nodes)
          .links(links)
          .start();

      // Update links.
      link = link.data(links, function(d) { return d.target.id; });

      link.exit().remove();

      link.enter().insert("line", ".node")
          .attr("class", "link");

      // Update nodes.
      node = node.data(nodes, function(d) { return d.id; });

      node.exit().remove();

      var nodeEnter = node.enter().append("g")
          .attr("class", "node")
          .on("click", click)
          .call(force.drag);

      nodeEnter.append("circle")
          .attr("r", function(d) { return d.number == 0 ? 6 : 4 });

      nodeEnter.append("text")
          .attr("dy", ".35em")
          .text(function(d) { return d.name; });

      node.select("circle")
          .style("fill", color);
    }

    function tick() {
      link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
    }

    function color(d) {
      //return d.number == 0 ? "#fd8d3c" : "#c6dbef" // collapsed package

      return d.number == selectedBlockNum ? "#00FF00" :
      (d.number == 0 ? "#fd8d3c" : "#c6dbef") // collapsed package
    }

    // Toggle children on click.
    function click(d) {
      if (d3.event.defaultPrevented) return; // ignore drag

      that.setState({
        selectedBlockNum: d.number,
      });
      update();
    }

    // Returns a list of all nodes under the root.
    function flatten(root) {
      var nodes = [], i = 0;

      function recurse(node) {
        if (node.children) node.children.forEach(recurse);
        if (!node.id) node.id = ++i;
        nodes.push(node);
      }

      recurse(root);
      return nodes;
    }
  }

  render() {
    const that = this;
    const {graph_blocks, columns, blocks_data, className, id, role} = this.props;

    if (graph_blocks == null)
    return (
      <div className={className} id={id} role={role}>
        <strong> No Blocks</strong>
      </div>)
    else
      return (
        <div className={className} id={id} role={role}>
          <div className='row'>
            <div className='col-12'>
              <div className='container'>
                <div className='chartContainer'>
                  <svg className='chart-block'/>
                </div>
              </div>
            </div>
          </div>
          <div className='row'>
            <div className='col-12'>
              <ReactTable data={blocks_data}
              defaultPageSize={10}
              minRows={0}
              columns={columns}
              className='-striped'
              getTrProps={(state, rowInfo) => {
                if (rowInfo && rowInfo.row) {
                  return {
                    onClick: (e) => {
                      that.setState({
                        selectedBlockNum: rowInfo.row.blockNum,
                      })
                    },
                    style: {
                      background: rowInfo.index === this.state.selectedBlockNum ? '#b8daff' :
                       rowInfo.index%2 == 0 ? 'rgba(0,0,0,.05)' : 'white',
                    }
                  }
                }else{
                  return {}
                }
              }} />
            </div>
          </div>
        </div>
      )
  }
}

Blocks.defaultProps = {
  graph_blocks: null,
  blocks_data: [],
  columns: [
  {
    id: 'blockNum',
    Header: 'Block Num',
    accessor: d => parseInt(d.header.block_num),
    width: 30,
  },
  { id: 'batchIds',
    Header: 'Batch Ids',
    accessor: d => d.header.batch_ids.map((i) => {
          return (  <Hash key={i} hash={i}/> )
        })
  },
  {
    id: 'consensus',
    Header: 'Consensus',
    accessor: d => d.header.consensus,
  },
    { id: 'prevBlockId',
    Header: 'Previous Block ID',
    accessor: d => <Hash hash={d.header.previous_block_id}/>,
  },
    { id: 'signerPublicKey',
    Header: 'Signer Public Key',
    accessor: d => <Hash hash={d.header.signer_public_key}/>,
  },
    { id: 'stateRootHash',
    Header: 'state Root Hash',
    accessor: d =><Hash hash={d.header.state_root_hash}/>,
  },]
};

function mapStateToProps(store) {
  return {
    graph_blocks: store.blocksReducer.data.graph,
    blocks_data: store.blocksReducer.data.data,
  };
}

export default connect (mapStateToProps, null)(Blocks);
