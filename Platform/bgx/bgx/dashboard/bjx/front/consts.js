export const nodes = {
  "net_structure":{
      "parent_node": [
      {
        "public_key": "0xFB2F7C8687F6d86a031D2DE3d51f4c62e83AdA22",
        "IP": "192.168.1.1",
        "port": 8080,
        "node_type": "leader",
        "node_state": "active",
        "children":[
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "permalink",
            "node_state": "inactive"
          },
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "secondary",
            "node_state": "active"
          }
        ]
      },

      {
        "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
        "IP": "192.168.1.2",
        "port": 8080,
        "node_type": "permalink",
        "node_state": "inactive",
        "children":[
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "permalink",
            "node_state": "inactive"
          },
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "secondary",
            "node_state": "active"
          }
        ]
      },

      {
        "public_key": "0xAF2F7C8687F6d86a031D2DE3d51f4c62e83AdA96",
        "IP": "192.168.1.3",
        "port": 8080,
        "node_type": "secondary",
        "node_state": "active",
        "children":[
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "permalink",
            "node_state": "inactive"
          },
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "secondary",
            "node_state": "active"
          }
        ]
      },

      {
        "public_key": "0xBF2F7C8687F6d86a031D2DE3d51f4c62e83AdA96",
        "IP": "192.168.1.4",
        "port": 8080,
        "node_type": "arbitrator",
        "node_state": "active",
        "children":[
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "permalink",
            "node_state": "inactive"
          },
          {
            "public_key": "0xAD2F7C8687F6d86a031D2DE3d51f4c62e83AdA36",
            "IP": "192.168.1.2",
            "port": 8080,
            "node_type": "secondary",
            "node_state": "inactive"
          }
        ]
      },
    ]
  }
}
