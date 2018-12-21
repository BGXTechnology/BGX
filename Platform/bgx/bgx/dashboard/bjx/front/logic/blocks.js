import { trimHash } from '../helpers/helper'

export function convertBlocks(data) {
    let parent = {name: 'initial'}
    let initial = parent
    data.data.forEach((i) => {
      i.header.block_num = parseInt(i.header.block_num)

      let node = {
        name: trimHash(i.header_signature),
        number: i.header.block_num,
        children: []
      }
      parent.children = [node]
      parent = node
    })

    data.graph = initial.children[0];
    data.data = data.data.reverse();
  return data;
}
