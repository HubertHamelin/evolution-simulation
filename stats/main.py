import pandas as pd
import networkx as nx
import matplotlib.pyplot as plt


SENSORY_NEURONS = ['Lx', 'Ly', 'BDx', 'BDy', 'LMx', 'LMy', 'Blr', 'Bfd', 'Osc', 'Pop']
SENSORY_NODE_COLOR = 'blue'
ACTION_NEURONS = ['Mfd', 'Mrv', 'Mri', 'Mlf', 'Mea', 'Mwe', 'Mno', 'Mso', 'Mra', 'Dno']
ACTION_NODE_COLOR = 'red'
HIDDEN_NODE_COLOR = 'gray'


def get_nodes_color(G: nx.DiGraph):
    nc = []
    for node in G.nodes:
        if node in SENSORY_NEURONS:
            nc.append(SENSORY_NODE_COLOR)
        elif node in ACTION_NEURONS:
            nc.append(ACTION_NODE_COLOR)
        else:
            nc.append(HIDDEN_NODE_COLOR)
    return nc


def get_edges_color(G: nx.DiGraph):
    ec = []
    for edge in G.edges:
        if G.edges[edge]['weight'] >= 0:
            ec.append('green')
        else:
            ec.append('red')
    return ec


def get_edges_width(G: nx.DiGraph):
    ew = []
    for edge in G.edges:
        ew.append(G.edges[edge]['weight'] / 2)
    return ew


if __name__ == '__main__':

    # src: https://networkx.org/documentation/latest/tutorial.html
    G = nx.DiGraph()

    # Get the brain data from the csv file produced by the java simulation
    data = pd.read_csv('./most_common_brain.csv', sep=';', header=None)

    # Make tuples from each connection data
    for index, row in data.iterrows():
        G.add_edge(row[0], row[1], weight=row[2])

    # Draw graph
    node_color = get_nodes_color(G)
    edge_color = get_edges_color(G)
    edge_width = get_edges_width(G)
    nx.draw(
        G,
        with_labels=True,
        arrows=True,
        arrowsize=10,
        node_color=node_color,
        edge_color=edge_color,
        font_size=8,
        width=edge_width
    )
    plt.show()
