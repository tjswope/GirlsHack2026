import pygame
from control import *
from board import *


class Cell:
    def __init__(self, x, y, image, value):
        self.x, self.y = x * CELLSIZE, y * CELLSIZE
        self.image = image
        self.value = value

    def __repr__(self):
        return self.type
    def double(self):
        self.value*=2
        self.image =
    def draw(self, board_surface):
        board_surface.blit(self.image, (self.x, self.y))
