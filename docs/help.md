# Help

Functionalities Ousi provides include network generation, transformation, analysis, and visualization. Plus, it is possible to save a network to your local disk and load it again next time. You can also write your own network in Ousi Adjacency List format and open it in Ousi.

* TOC
{:toc}

## Basics

At the top of the window is a menu bar. The left part consists of the network visualization area and the output explorer. On the right hand side is a network list where you can see all the networks in the memory at the moment and choose the networks to play with. Right-click a network and choose `Edit`, you will be able to edit the label and description of the network, which will be helpful when you are working with multiple networks at the same time.

## Visualization

There are two ways to visualize networks in Ousi.

The first approach is to right-click the network you want to visualize and click `Visualize`. You can also choose up to two networks before you right-click and click `Visualize`. This will display them at the same time.

The second approach is even simpler. You can double click a network and it will be visualized.

## Network generation

You may choose to generate complete networks or random simple networks based on the probability that there exists an edge between two vertices. It is possible to generate directed/undirected, weighted/unweighted networks, and if you choose to generate a weighted network, you can determine the lower and upper bounds of the weights.

1. Choose `Generate` menu;
2. Choose `Complete network` or `Random network`;
3. Fill in the parameters as required.

The newly generated network will appear in the network list in the right hand side.

## Network transformation

Network transformation algorithms generate new networks with existing ones.

### Addition

This will generate a new network, which is the sum of the networks you selected. Please be noted that it is possible to choose more than two networks at a time. Ousi will chain the addition automatically and produce a single output.

1. Choose networks from the network list;
2. Choose `Transform`;
3. Choose `Add`.

### Edge filtering

This will filter out all the edges whose weights are below certain threshold you set. Again, you can choose multiple networks from the list at a time and Ousi will perform the filtering to every single of them.

1. Choose networks from the network list;
2. Choose `Transform`;
3. Choose `Filter edge`;
4. Fill in the threshold as required.

## Network analysis

Ousi currently supports density analysis and QAP correlation analysis.

### Density analysis

This will calculate the densities of the networks you select. Ousi will judge whether they are directed and undirected networks and do the calculation correspondingly.

1. Choose networks from the network list;
2. Choose `Analyze`;
3. Choose `Density`.

### QAP correlation analysis

This will calculate the correlation between two networks and perform a QAP analysis to get the significance of this correlation.

1. Choose two networks from the network list;
2. Choose `Analyze`;
3. Choose `QAP`.

## Saving networks

To save a network, right-click that network in the list and choose the format you would like to download the network in. There are three possible options:

1. Binary, only Ousi and read and load again, saving lots of disk space when the network is large;
2. Adjacency list, an Ousi-specific format that human can read, mostly storing the adjacency list of the network;
3. DOT, the famous `GraphViz` format, wonderful for more complex visualization.

You can also select the network you want to download before you do the right-click. This will download all the networks you select.

## Loading networks

1. Choose `File`;
2. Choose `Open`;
3. Click the `upload` button or drag the files into the uploading area.

Please be noted that this only supports networks stored in the Ousi Binary format (`*.obin`) and Ousi Adjacency List Format (`*.oal`). It is possible to upload multiple files at a time.

## Ousi Adjacency List Format

A Ousi Adjacency List Format is a file format used by Ousi to represent a network. Its extension name is `.oal`. An `oal` file is made up of four parts.

1. Label and description (optional);
2. Information about whether it is a directed/undirected, weighted/unweighted network;
3. The vertex list (optional);
4. The adjacency list.

An example is given as follows.

![Sample network](/images/a.png)

For the network above, the Ousi Adjacency List Format file will look like this:

```
[a My sample network]
0 1
0 1 2 3
0 1 5 2 7 3 6
2 3 8
```

The first line is the label and description. They are bracketed between a pair of `[]`. The first word is the label. All of the rest makes the description. This is totally optional. You can drop this line or you may only want a label without a description.

The second line is made up of two binary numbers. The first indicates whether it is a directed network (0 for undirected and 1 for directed), and the second indicates whether it is a weighted network (0 for unweighted and 1 for weighted).

The third line, the vertex list, is also optional. By putting it there, you can tell Ousi which are the vertices that will appear in the adjacency list. This is useful in two ways. First, it defines an order in which the enumeration of vertex will appear. This is relevant in some algorithms. Second, isolated vertices, which are connected to no others, can only be defined in this way.

The rest of the file is the adjacency list. Each line represents a vertex. The first number is the vertex that this line is describing. It is followed by another number representing the vertex an edge ends with. If it is a weighted network, then the third number (which can be a float number) is the weight of that edge. If not, the third number will be describing the ending vertex of another edge.

If any vertex is not in the vertex list, this vertex will automatically be added.

For undirected graphs, you only have to describe each edge for exactly one time. For example, for edge `AB`, you either define it in the line for vertex `A` or vertex `B`. If you define it twice, there will be two edge undirected edges between the two vertices.

Note that the numbers you use to represent the vertices are no longer kept when Ousi reads your file. They are just temporary labels rather than eternal identifiers.