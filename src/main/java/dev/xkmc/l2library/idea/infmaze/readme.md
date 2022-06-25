# Random Access Infinite Maze Generation

## Definition
### Cell
A cell is a cubic space with a 3D position 
(denoting the smallest values of each axis) 
and an integer `scale`. 
The edge length of the cell is `2^scale`.
The coordinates of the vertexes of the cell are 
integer multiple of the edge length.
All sub-cells of a cell should be interconnected 
through paths within the cell.

### Wall
A wall is a square area between 2 cells of the same scale.
A wall has a 3D position
(denoting the smallest values of each axis),
a normal vector, and a scale.
A wall is open when one side can pass to the other.

## Generation procedure
First start from a grid of cells with maximum scale.
The largest cells have all walls open.

To access the state of a cell, the algorithm will traverse
down from the top cell, and full determine the immediate
state of the cell and its walls. The immediate state of
a cell includes the open-ness of the internal walls.
The immediate state of walls include the open-ness of
its children.


