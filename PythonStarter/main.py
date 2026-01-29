import pygame
import sys
import random
import math
import os
from tile import *

#declaring variables
pygame.init()
FPS = 60
WIDTH, HEIGHT = 800, 800
ROW, COL = 4, 4
RECT_HEIGHT = HEIGHT//ROW
RECT_WIDTH = WIDTH//COL
OL_COLOR = (187, 173, 160)
OL_THICKNESS = 10
BGCOLOR = (205, 192, 180)
FONT_COLOR = (119, 110, 101)
FONT = pygame.font.SysFont("comicsans", 60, bold=True)
VEL = 20
WINDOW = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("2048")

def draw(window, tiles):
    window.fill(BGCOLOR)
    #drawing all the tiles
    for tile in tiles.values():
        tile.draw(window)
    #draws the darker grey lines for the grid
    for row in range(1, ROW):
        y = row * RECT_HEIGHT
        pygame.draw.line(window, OL_COLOR, (0, y), (WIDTH, y), OL_THICKNESS)
    for col in range(1, COL):
        x = col * RECT_WIDTH
        pygame.draw.line(window, OL_COLOR, (x, 0), (x, HEIGHT), OL_THICKNESS)
    #lines that go across the whole screen
    pygame.draw.rect(window, OL_COLOR, (0, 0, WIDTH, HEIGHT), OL_THICKNESS)
    pygame.display.update()

#randomly generates tiles somewhere
def get_random_pos(tiles):
    row = None
    col = None
    while True:
        row = random.randrange(0, ROW)
        col = random.randrange(0, COL)
        #checks to make sure there isn't already a tile there
        if f"{row}{col}" not in tiles:
            break
    return row, col

#as we merge and move tiles we will be removing tiles from the grid and adding tiles
#this function keeps the overarching tiles list updated
def update_tiles(window, tiles, sorted_tiles):
    tiles.clear()
    for tile in sorted_tiles:
        tiles[f"{tile.row}{tile.col}"] = tile
    draw(window, tiles)

#checks to see if the board is full and game is over
#otherwise it generates a new tile at a random position that is either a 2 or a 4
def end_tiles(tiles):
    if len(tiles) == 16:
        return "Game Over"
    row, col = get_random_pos(tiles)
    tiles[f"{row}{col}"] = Tile(random.choice([2, 4]), row, col)
    return "continue"

def move_tiles(window, tiles, clock, direction):
    updated = True
    #ensures that we don't merge twice in one move
    #Example:
    #[2][2][2] should become [4][2], but without this set it would become [8] because the [2] could merge twice creating [4][4]
    blocks = set()
    if direction == "left":
        #specifies to the sort function later that we want to sort the tiles by the col value
        #as opposed to the row value or value value
        sortfunc = lambda x : x.col
        #False - smallest to largest
        #True - largest to smallest
        reverse = False
        #how much the tile should move by each frame, moving left so to the negative x direction
        movediff = (-VEL, 0)
        #function returns true or false whether or not the tile.col is 0, if it is 0 that means that
        #it is already at the very edge and can't move any further left
        bounds = lambda tile : tile.col == 0
        #gets the tile that is to the left of the current tile. Later used for merging and moving
        #purposes
        next = lambda tile : tiles.get(f"{tile.row}{tile.col - 1}")
        #for animation purposes. checks if the tile is in the right position yet for merging when
        #we know that the space next to a tile is definitely a tile and not just empty space, and
        #to merge the bounds to stop is going to be the leftmost side of the tile
        merge_check = lambda tile, next_tile : tile.x > next_tile.x + VEL
        #for animation purposes. checks if the tile is in the right position. needs to be a different
        #function because it's empty space right next to it, we are going until the next tile. and the
        #boundary to stop is different, it's the very right of the tile, not the left
        move_check = lambda tile, next_tile : tile.x > next_tile.x + RECT_WIDTH + VEL
        #when we are animating the tiles are often going to be inbetween columns and rows. This variables
        #tells the setpos() in tile whether to round up or down accordingly to determine the colunn that
        #the tile is in
        round = True
    #the same logic from above applies to all of the other three directions with minor tweaks to account
    #for the different direction that the tiles are moving in
    elif direction == "right":
        sortfunc = lambda x : x.col
        reverse = True
        movediff = (VEL, 0)
        bounds = lambda tile : tile.col == COL - 1
        next = lambda tile : tiles.get(f"{tile.row}{tile.col + 1}")
        merge_check = lambda tile, next_tile : tile.x < next_tile.x - VEL
        move_check = lambda tile, next_tile : tile.x + RECT_WIDTH + VEL < next_tile.x
        round = False
    elif direction == "up":
        sortfunc = lambda y : y.row
        reverse = False
        movediff = (0, -VEL)
        bounds = lambda tile : tile.row == 0
        next = lambda tile : tiles.get(f"{tile.row - 1}{tile.col}")
        merge_check = lambda tile, next_tile : tile.y > next_tile.y + VEL
        move_check = lambda tile, next_tile : tile.y > next_tile.y + RECT_HEIGHT + VEL
        round = True
    elif direction == "down":
        sortfunc = lambda y : y.row
        reverse = True
        movediff = (0, VEL)
        bounds = lambda tile : tile. row == ROW - 1
        next = lambda tile : tiles.get(f"{tile.row + 1}{tile.col}")
        merge_check = lambda tile, next_tile : tile.y < next_tile.y - VEL
        move_check = lambda tile, next_tile : tile.y + RECT_HEIGHT + VEL < next_tile.y
        round = False

    while updated:
        clock.tick(FPS)
        #updated starts as False, the only way for it to be true is if it enters the
        #for loop and meets one of the requirements for merge, or move
        #otherwise it will hit a continue where the for loop is exited and updated remains False
        #when nothing is able to move or merge then we know that we can stop the animation checking
        #because there's nothing to animate
        updated = False
        #sorts the tiles so that we check them in the right order
        sort_tiles = sorted(tiles.values(), key=sortfunc, reverse=reverse)
        #we use enumerate here so we can also keep track of the index in case we need to merge
        #and pop the tile from the sorted_tiles list
        for i, tile in enumerate(sort_tiles):
            #if we are at a boundary we can't move the tile at all
            if bounds(tile):
                continue
            #gets the tile next to the tile we are moving
            next_tile = next(tile)
            #if there isn't a tile there then we can just move the tile because it's empty space
            if not next_tile:
                tile.move(movediff)
            #this is to check if merging is possible
            #in order for merging to occur three conditions must be met: the values of the current tile
            #and the tile next to it is the same, this tile is not in blocks (meaning it has not merged yet)
            #and the tile next to it is also not in blocks (meaning it also has not been merged yet)
            elif tile.value == next_tile.value and tile not in blocks and next_tile not in blocks:
                #if merge_check returns true, meaning that the tile has not completely moved to the correct spot,
                #we continue to move the tile
                if merge_check(tile, next_tile):
                    tile.move(movediff)
                #otherwise we can change the lists and stuff to make it so the new merged tile appears
                #multiply by two to denote the new value to the tile
                #update the image of the tile
                #remove one of the tiles because you just merged two tiles into 1
                #add the merged tile to blocks so that it won't be merged again this move
                else:
                    next_tile.value *= 2
                    next_tile.update_image()
                    sort_tiles.pop(i)
                    blocks.add(next_tile)
            #if the tile can't be merged with the tile next to it we check if there's empty space between
            #it and if so then we would move the tile to fill the empty space next to it
            elif move_check(tile, next_tile):
                tile.move(movediff)
            #if we have a formation like [2][4] where none of the above actions are applicable we do nothing
            else:
                continue
            #adjusts the row and col values for the tiles currently in animation
            tile.set_pos(round)
            updated = True
        #updates the big list of tiles on the board
        update_tiles(window, tiles, sort_tiles)
    #adds a two or four at the end of the turn
    return end_tiles(tiles)

#creates tiles at the very beginning of the gamae
def generate_tiles():
    tiles = {}
    for _ in range(2):
        row, col = get_random_pos(tiles)
        tiles[f"{row}{col}"] = Tile(2, row, col)
    return tiles

def main(window):
    clock = pygame.time.Clock()
    run = True
    tiles = generate_tiles()
    while run:
        clock.tick(FPS)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                break
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    move_tiles(window, tiles, clock, "left")
                if event.key == pygame.K_RIGHT:
                    move_tiles(window, tiles, clock, "right")
                if event.key == pygame.K_UP:
                    move_tiles(window, tiles, clock, "up")
                if event.key == pygame.K_DOWN:
                    move_tiles(window, tiles, clock, "down")
        draw(window, tiles)
    pygame.quit()
if __name__ == "__main__":
    main(WINDOW)
