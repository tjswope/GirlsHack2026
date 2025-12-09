import pygame
from control import *
from cell import *

class Board:
    def __init__(self):
        self.board_surface = pygame.Surface((HEIGHT, WIDTH))
        self.board_list = [[Cell(col, row, blank, "E") for row in range(ROW)] for col in range(COL)]
        self.dug = []

    def left(self):

    def right(self):

    def up(self):

    def down(self):

    def draw(self, screen):
        for r in self.board_list:
            for c in r:
                c.draw(self.board_surface)
        screen.blit(self.board_surface, (0, 0))

    def display_board(self):
        for r in self.board_list:
            print(r)