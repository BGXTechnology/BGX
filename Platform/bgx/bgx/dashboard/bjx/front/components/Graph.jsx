import React from 'react'
import {ForceGraph, ForceGraphNode, ForceGraphLink} from 'react-vis-force';
import axios from 'axios';
import * as d3 from "d3";
import { connect } from 'react-redux'

import { selectPeer as selectP } from '../actions/actions';

let createHandlers = function(dispatch) {
  let selectPeer = function(id) {
    dispatch(selectP(id))
  };

  return {
    selectPeer,
    // other handlers
  };
}

class Graph extends React.Component {
  constructor(props) {
    super(props);

    this.handlers = createHandlers(this.props.dispatch);
  }

  componentDidUpdate() {
    const data = Object.assign({}, this.props.data);

var width = +d3.select('.chart').style('width').slice(0, -2),
    height = 300,
    root;


    var force = d3.layout.force()
    .linkDistance(60)
    .charge(-120)
    .gravity(.05)
    .size([width, height])
    .on("tick", tick);

var svg = d3.select(".chart")
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

  var div = d3.select(".chartContainer").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0)
    .style("position", "absolute");

  var nodeEnter = node.enter().append("g")
      .attr("class", "node")
      .on("click", click)
      .on("dblclick", dblClick)
      .on("mouseover", mouseOver)
      .on("mouseout", mouseLeave)
      .call(force.drag);

  nodeEnter.append("circle")
      .attr("r", function(d) { return 5 });

  nodeEnter.append("text")
      .attr("dy", "-1em")
      .text(function(d) { return d.IP; })
      .style("opacity",  function(d) { return d.node_state == 'active' ? 1 : 0.3; })
      .style("font-weight",  function(d) { return d.node_state == 'active' ? 'bold' : 'normal'; })
  node.select("circle")
   .attr("r", function(d) {
        return (d.ssselect === undefined || !d.ssselect ? 5 : 10) });

  node.select("circle")
      .style("fill", color)
      .style("opacity",  function(d) { return d.node_state == 'active' ? 1 : 0.3; })
}

function tick() {
  link.attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
}

function color(d) {
  return d.ssselect ? "#00FF00"
      : d._children ? "#3182bd" // collapsed package
      : "#c6dbef" // expanded package
}

function mouseLeave(d){
  var div = d3.select(".tooltip")
    div
    .style("opacity", 0)
    .style("left", "-100px")
   .style("top", "-100px");
  }

function mouseOver(d){
  var div = d3.select(".tooltip")
  div
    .style("opacity", .9);
  div.html("IP: "+d.IP + "<br/>"+
            d.node_state +"<br/>"+
            d.node_type)
   .style("left", (d.x) + "px")
   .style("top", (d.y + 12) + "px")

}

// Toggle children on click.
function dblClick(d) {
  if (d3.event.defaultPrevented) return; // ignore drag
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
  update();
}

function click(d) {
  if (d3.event.defaultPrevented) return; // ignore drag
    unselect(root)
  d.ssselect = true;
  store.dispatch(selectP(d))
  update();
}
function unselect(root) {

  root.ssselect = false
    if (root.children === undefined || root.children == null)
    return;
  root.children.map((c) => {unselect(c)});
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
    return( <div className='container'>
        <div className='chartContainer'>
          <svg className='chart'>
          </svg>
        </div>
      </div>);
  }
}

function mapStateToProps(store) {
  return {
    data: store.peersReducer.data,
  };
}

export default connect (mapStateToProps, null)(Graph);
