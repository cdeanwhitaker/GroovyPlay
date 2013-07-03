#!/usr/bin/env groovy

MAXX = 110
MAXY = 36

TESTSEED_DIE_1 = [ [6,4], [4,5], [5,5] ]
TESTSEED_LIVE_1 = [ [1,1], [1,2], [1,3], [2,1], [2,2], [2,3] ]
TESTSEED_DIE_2 = [ [5,5] ]
TESTSEED_BLOCK = [ [1,1], [2,1], [1,2], [2,2] ]
TESTSEED_BLINKER = [ [1,1], [2,1], [3,1] ]
TESTSEED_TOAD = [ [2,4], [2,5], [2,6], [1,5], [1,6], [1,7] ]
TESTSEED_GLIDER = [ [2,1], [3,2], [3,3], [1,3], [2,3], [3,3] ]
TESTSEED_BOX_TOP_LEFT = [ [0,0], [1,0], [0,1], [1,1] ]
TESTSEED_BOX_TOP_RIGHT = [ [MAXX-2,0], [MAXX-1,0], [MAXX-2,1], [MAXX-1,1] ]
TESTSEED_BOX_BOTTOM_LEFT = [ [0,MAXY-2], [1,MAXY-2], [0,MAXY-1], [1,MAXY-1] ]
TESTSEED_BOX_BOTTOM_RIGHT = [ [MAXX-2,MAXY-2], [MAXX-1,MAXY-2], [MAXX-2,MAXY-1], [MAXX-1,MAXY-1] ]

// Chars for cell based on value
def getCell = { i -> return [0:" ", 1:"*"] [i] }

// Random number between zero and given bound
def getRandInt = { b -> return (new Random()).nextInt (b) }

// Zero the whole grid
def initGrid = { return (1..MAXY).collect { (1..MAXX).collect { 0 } } }

// Display the passed in grid
def showGrid = { g ->
	for (int y=0; y<g.size; y++) {
		for (int x=0; x<g[y].size; x++) {
			print getCell ( g[y][x] )
		}
		println ""
	}
	for (int k=0; k<g[0].size; k++) {
		print "~"
	}
	println ""
}

// Return true if coords fall within proper grid range
def inbounds = { x, y ->
	if ( (x >= 0) && (y >= 0) && (x < MAXX) && (y < MAXY) ) {
		return true
	}
}

// Count neighbors
def getNumNeighbors = { xy, g ->
	x = xy[0]
	y = xy[1]
	neighborCount = 0

	if ( inbounds (x-1, y-1) && g[y-1][x-1] == 1 ) {
		neighborCount++
	}
	if ( inbounds (x, y-1) && g[y-1][x] == 1 ) {
		neighborCount++
	}
	if ( inbounds (x+1, y-1) && g[y-1][x+1] == 1 ) {
		neighborCount++
	}

	if ( inbounds (x-1, y) && g[y][x-1] == 1 ) {
		neighborCount++
	}
	if ( inbounds (x+1, y) && g[y][x+1] == 1 ) {
		neighborCount++
	}

	if ( inbounds (x-1, y+1) && g[y+1][x-1] == 1 ) {
		neighborCount++
	}
	if ( inbounds (x, y+1) && g[y+1][x] == 1 ) {
		neighborCount++
	}
	if ( inbounds (x+1, y+1) && g[y+1][x+1] == 1 ) {
		neighborCount++
	}
	return neighborCount
}

// Return a grid of neighbor counts
def getNeighborGrid = { g ->
	ng = []
	for (int y=0; y<g.size; y++) {
		row = []
		for (int x=0; x<g[y].size; x++) {
			row.add ( getNumNeighbors ([x,y], g) )
		}
		ng.add (row)
	}
	return ng
}

// Apply Conway's life rules based on previous iteration and neighbor count
def translateGeneration = { old, ng ->
	g = []
	for (int y=0; y<ng.size; y++) {
		row = []
		for (int x=0; x<ng[y].size; x++) {
			if ( (ng[y][x] == 3) || (ng[y][x] == 2 && old[y][x] == 1) ) {
				row.add (1)
			} else {
				row.add (0)
			}
		}
		g.add (row)
	}
	return g
}

def main = { seed ->
	grid = initGrid()
	for (int s=0; s<seed.size; s++) {
		grid [seed[s][1]] [seed[s][0]] = 1
	}
	showGrid (grid)
	while (true) {
		Thread.sleep (500)
		ng = getNeighborGrid (grid)
		grid = translateGeneration (grid, ng)
		showGrid (grid)
	}
}

// Generate a random seed of random size
def getSeed = {
	seed = []
	seedSize = getRandInt ( (new Float (MAXX*MAXY/2)).intValue() )
	for (int i=0; i<seedSize; i++) {
		seed.add ( [ getRandInt (MAXX), getRandInt (MAXY) ] )
	}
	return seed
}

main ( getSeed() )

