
import pygame
import os

#constants
CELLSIZE = 128
ROW = 4
COL = 4
WIDTH = CELLSIZE * ROW
HEIGHT = CELLSIZE * COL
FPS = 24
FILLCOLOR = (40, 40, 40)
TITLE = "2048"

#image imports
cell_numbers = []
for i in range(1, 11):
    cell_numbers.append(pygame.transform.scale(pygame.image.load(os.path.join("images", f"{2**i}.png")), (CELLSIZE, CELLSIZE)))
blank = pygame.transform.scale(pygame.image.load(os.path.join("images", "blank.png")), (CELLSIZE, CELLSIZE))
