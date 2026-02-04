import os
import pygame
import math
from main import *

class Tile:
    def __init__(self, val, row, col):
        self.value = val
        self.row = row
        self.col = col
        self.x = col * RECT_WIDTH
        self.y = row * RECT_HEIGHT
        self.image = pygame.transform.scale(pygame.image.load(os.path.join("images", f"{self.value}.png")), (RECT_WIDTH, RECT_HEIGHT))

    def draw(self, window):
        window.blit(self.image, (self.x, self.y))

    def update_image(self):
        self.image = pygame.transform.scale(pygame.image.load(os.path.join("images", f"{self.value}.png")),(RECT_WIDTH, RECT_HEIGHT))

    def move(self, dist):
        self.x += dist[0]
        self.y += dist[1]

    def set_pos(self, round=False):
        if round:
            #ceil means you round up regardless
            self.row = math.ceil(self.y / RECT_HEIGHT)
            self.col = math.ceil(self.x / RECT_WIDTH)
        else:
            #floor means you round down regardless
            self.row = math.floor(self.y / RECT_HEIGHT)
            self.col = math.floor(self.x / RECT_WIDTH)

