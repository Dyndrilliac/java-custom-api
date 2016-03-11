/*
 * Title: Health Grid (COT 3210 Programming Project)
 * Author: Matthew Boyette
 * Date: 3/22/2014
 * 
 * This class provides a graphical display to the user that shows a simulation of an infection spreading through a population operating under certain
 * fundamental rules.
 */

package api.gui.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

import api.util.Mathematics;

public class HealthGrid extends JPanel
{
    protected final static class Cell extends JPanel
    {
        private final static long serialVersionUID = 1L;
        private int               column           = 0;
        private CellContent       content          = null;
        private int               row              = 0;
        
        public Cell(final int x, final int y)
        {
            this(x, y, State.EMPTY);
        }
        
        public Cell(final int x, final int y, final State initialState)
        {
            super();
            this.setColumn(x);
            this.setRow(y);
            this.setContent(new CellContent(this, initialState));
            this.add(this.getContent());
        }
        
        public final int getColumn()
        {
            return this.column;
        }
        
        public final CellContent getContent()
        {
            return this.content;
        }
        
        public final int getRow()
        {
            return this.row;
        }
        
        public final void setColumn(final int column)
        {
            this.column = column;
        }
        
        public final void setContent(final CellContent content)
        {
            this.content = content;
        }
        
        public final void setRow(final int row)
        {
            this.row = row;
        }
    }
    
    protected final static class CellContent extends JPanel
    {
        private final static long serialVersionUID = 1L;
        private Cell              parent           = null;
        private State             state            = null;
        
        public CellContent(final Cell parent)
        {
            this(parent, State.EMPTY);
        }
        
        public CellContent(final Cell parent, final State initialState)
        {
            super();
            this.setCellParent(parent);
            this.setState(initialState);
        }
        
        public final Cell getCellParent()
        {
            return this.parent;
        }
        
        public final State getState()
        {
            return this.state;
        }
        
        @Override
        protected void paintComponent(final Graphics g)
        {
            super.paintComponent(g);
            
            switch (this.getState())
            {
                case ALIVE:
                    
                    g.setColor(Color.GREEN);
                    break;
                
                case EMPTY:
                    
                    g.setColor(Color.BLACK);
                    break;
                
                case INFECTED:
                    
                    g.setColor(Color.RED);
                    break;
                
                default:
                    
                    break;
            }
            
            g.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        
        public void randomize()
        {
            final int STATE = Mathematics.getRandomInteger(1, 2, true);
            
            switch (STATE)
            {
                case 1:
                    
                    this.setState(State.ALIVE);
                    break;
                
                case 2:
                    
                    this.setState(State.EMPTY);
                    break;
                
                default:
                    
                    break;
            }
        }
        
        public final void setCellParent(final Cell parent)
        {
            this.parent = parent;
        }
        
        public final void setState(final State state)
        {
            this.state = state;
            this.repaint();
        }
    }
    
    protected static enum State
    {
        ALIVE, EMPTY, INFECTED
    }
    
    private final static long serialVersionUID = 1L;
    private Cell[][]          gridOfCells      = null;
    private int               numColumns       = 0;
    private int               numRows          = 0;
    
    public HealthGrid(final int rows, final int columns)
    {
        super();
        
        int newRows = 1, newColumns = 1;
        
        if (rows > 1)
        {
            newRows = rows;
        }
        
        if (columns > 1)
        {
            newColumns = columns;
        }
        
        this.setLayout(new GridLayout(newRows, newColumns, 0, 0));
        this.setNumberOfColumns(newColumns);
        this.setNumberOfRows(newRows);
        this.initializeConfiguration();
    }
    
    protected int countNeighbors(final Cell[][] grid, final Cell cell, final State state, final int numPossibleNeighbors)
    {
        int count = 0;
        
        for (int i = 1; i <= numPossibleNeighbors; i++)
        {
            Cell neighbor;
            
            try
            {
                int x = cell.getColumn(), y = cell.getRow();
                
                switch (i)
                {
                    case 1:
                        
                        neighbor = grid[x][y + 1]; // N
                        break;
                    
                    case 2:
                        
                        neighbor = grid[x][y - 1]; // S
                        break;
                    
                    case 3:
                        
                        neighbor = grid[x - 1][y]; // E
                        break;
                    
                    case 4:
                        
                        neighbor = grid[x + 1][y]; // W
                        break;
                    
                    case 5:
                        
                        neighbor = grid[x - 1][y + 1]; // NW
                        break;
                    
                    case 6:
                        
                        neighbor = grid[x + 1][y + 1]; // NE
                        break;
                    
                    case 7:
                        
                        neighbor = grid[x - 1][y - 1]; // SW
                        break;
                    
                    case 8:
                        
                        neighbor = grid[x + 1][y - 1]; // SE
                        break;
                    
                    default:
                        
                        neighbor = null;
                        break;
                
                }
            }
            catch (final Exception e)
            {
                neighbor = null;
            }
            
            if ((neighbor != null) && (neighbor.getContent().getState() == state))
            {
                count++;
            }
        }
        
        return count;
    }
    
    public final Cell[][] getGridOfCells()
    {
        return this.gridOfCells;
    }
    
    public final int getNumberOfColumns()
    {
        return this.numColumns;
    }
    
    public final int getNumberOfRows()
    {
        return this.numRows;
    }
    
    public void infectNeighbors(final Cell[][] grid, final Cell cell, final int numPossibleNeighbors)
    {
        for (int i = 1; i <= numPossibleNeighbors; i++)
        {
            Cell neighbor;
            
            try
            {
                int x = cell.getColumn(), y = cell.getRow();
                
                switch (i)
                {
                    case 1:
                        
                        neighbor = grid[x][y + 1]; // N
                        break;
                    
                    case 2:
                        
                        neighbor = grid[x][y - 1]; // S
                        break;
                    
                    case 3:
                        
                        neighbor = grid[x - 1][y]; // E
                        break;
                    
                    case 4:
                        
                        neighbor = grid[x + 1][y]; // W
                        break;
                    
                    case 5:
                        
                        neighbor = grid[x - 1][y + 1]; // NW
                        break;
                    
                    case 6:
                        
                        neighbor = grid[x + 1][y + 1]; // NE
                        break;
                    
                    case 7:
                        
                        neighbor = grid[x - 1][y - 1]; // SW
                        break;
                    
                    case 8:
                        
                        neighbor = grid[x + 1][y - 1]; // SE
                        break;
                    
                    default:
                        
                        neighbor = null;
                        break;
                
                }
            }
            catch (final Exception e)
            {
                neighbor = null;
            }
            
            if ((neighbor != null) && (neighbor.getContent().getState() == State.ALIVE))
            {
                neighbor.getContent().setState(State.INFECTED);
            }
        }
    }
    
    public void initializeConfiguration()
    {
        final Cell[][] GRID = new Cell[this.getNumberOfColumns()][this.getNumberOfRows()];
        
        this.removeAll();
        this.setGridOfCells(GRID);
        
        for (int y = 0; y < this.getNumberOfRows(); y++)
        {
            for (int x = 0; x < this.getNumberOfColumns(); x++)
            {
                GRID[x][y] = new Cell(x, y);
                this.add(GRID[x][y]);
            }
        }
        
        this.validate();
    }
    
    public void injectInfection()
    {
        final int MAX_Z = ((Double)Math.pow(Math.max((double)this.getNumberOfColumns(), (double)this.getNumberOfRows()), 3.0)).intValue();
        
        int x = 0, y = 0, z = 0;
        
        do
        {
            x = Mathematics.getRandomInteger(0, this.getNumberOfColumns(), false);
            y = Mathematics.getRandomInteger(0, this.getNumberOfRows(), false);
            z++;
        }
        while ((this.getGridOfCells()[x][y].getContent().getState() != State.ALIVE) && (z < MAX_Z));
        
        if (z < (MAX_Z - 1))
        {
            this.getGridOfCells()[x][y].getContent().setState(State.INFECTED);
        }
    }
    
    protected void iterateCell(final Cell[][] grid, final Cell cell, final int num1, final int num2, final int num3, final boolean diagonalNeighbors)
    {
        int numPossibleNeighbors = 4, numNeighbors = 0;
        
        if (diagonalNeighbors)
        {
            numPossibleNeighbors = 8;
        }
        
        switch (cell.getContent().getState())
        {
            case ALIVE: // An alive cell with less than num2 or more than num3 alive neighbors becomes empty.
                
                numNeighbors = this.countNeighbors(grid, cell, State.ALIVE, numPossibleNeighbors);
                
                if ((numNeighbors < num2) || (numNeighbors > num3))
                {
                    cell.getContent().setState(State.EMPTY);
                }
                break;
            
            case EMPTY: // An empty cell with num1 or more alive neighbors becomes alive.
                
                numNeighbors = this.countNeighbors(grid, cell, State.ALIVE, numPossibleNeighbors);
                
                if (numNeighbors >= num1)
                {
                    cell.getContent().setState(State.ALIVE);
                }
                break;
            
            case INFECTED: // An infected cell spreads its infection to its alive neighbors and then becomes empty.
                
                this.infectNeighbors(grid, cell, numPossibleNeighbors);
                cell.getContent().setState(State.EMPTY);
                break;
            
            default:
                
                break;
        
        }
    }
    
    public void iterateConfiguration(final String command)
    {
        int num1 = 3, num2 = 2, num3 = 3;
        boolean diagonalNeighbors = false;
        
        switch (command)
        {
            case "Iterate B":
                
                num1 = 2;
                num2 = 2;
                num3 = 2;
                break;
            
            case "Iterate C":
                
                diagonalNeighbors = true;
                break;
            
            default:
                
                break;
        }
        
        for (int y = 0; y < this.getNumberOfRows(); y++)
        {
            for (int x = 0; x < this.getNumberOfColumns(); x++)
            {
                this.iterateCell(this.getGridOfCells(), this.getGridOfCells()[x][y], num1, num2, num3, diagonalNeighbors);
            }
        }
    }
    
    public void randomizeConfiguration()
    {
        for (int y = 0; y < this.getNumberOfRows(); y++)
        {
            for (int x = 0; x < this.getNumberOfColumns(); x++)
            {
                this.getGridOfCells()[x][y].getContent().randomize();
            }
        }
    }
    
    public final void setGridOfCells(final Cell[][] gridOfCells)
    {
        this.gridOfCells = gridOfCells;
    }
    
    public final void setNumberOfColumns(final int numberOfColumns)
    {
        this.numColumns = numberOfColumns;
    }
    
    public final void setNumberOfRows(final int numberOfRows)
    {
        this.numRows = numberOfRows;
    }
}