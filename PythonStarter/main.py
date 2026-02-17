import pygame
import random
from settings import *
from tile import Tile

pygame.init()

FPS = 60

GRID_LINE_COLOR = (187, 173, 160)
GRID_LINE_THICKNESS = 10
BACKGROUND_COLOR = (205, 192, 180)

WINDOW = pygame.display.set_mode((WIDTH, WINDOW_HEIGHT))
pygame.display.set_caption("2048")

FONT = pygame.font.SysFont("comicsans", 60, bold=True)

# -----------------------------
# Board creation + rendering
# -----------------------------
def create_board():
    """Return an empty ROW x COL grid of tiles (None = empty cell)."""
    return [[None for _ in range(COL)] for _ in range(ROW)]


def draw_hud(window, score):
    """Draw a simple score bar at the top."""
    pygame.draw.rect(window, BACKGROUND_COLOR, (0, 0, WIDTH, HUD_HEIGHT))
    text = FONT.render(f"Score: {score}", True, (50, 50, 50))
    window.blit(text, (20, 15))

#TODO
def has_moves_left(tiles):
    """
    True if:
      - any empty cell exists, or
      - any horizontal/vertical adjacent tiles have equal value
    """
    # Check if empty cell exists


    # Check if adjacent equal tiles exist


    return False


def draw(window, tiles, score, game_over=False):
    """Draw the background, tiles, grid lines, and HUD."""
    window.fill(BACKGROUND_COLOR)

    # Draw tiles
    for r in range(ROW):
        for c in range(COL):
            tile = tiles[r][c]
            if tile is not None:
                # Shift tiles down so the HUD does not cover the board
                window.blit(tile.image, (tile.x, tile.y + BOARD_Y))

    # Draw internal grid lines
    for row in range(1, ROW):
        # Shift horizontal lines down so they align with the shifted board
        y = BOARD_Y + row * RECT_HEIGHT
        pygame.draw.line(window, GRID_LINE_COLOR, (0, y), (WIDTH, y), GRID_LINE_THICKNESS)

    for col in range(1, COL):
        x = col * RECT_WIDTH
        pygame.draw.line(
            window,
            GRID_LINE_COLOR,
            (x, BOARD_Y),
            (x, BOARD_Y + HEIGHT),
            GRID_LINE_THICKNESS,
        )

    # Outer border (shifted down)
    pygame.draw.rect(window, GRID_LINE_COLOR, (0, BOARD_Y, WIDTH, HEIGHT), GRID_LINE_THICKNESS)

    # HUD on top
    draw_hud(window, score)

    # Optional game-over overlay
    if game_over:
        overlay = pygame.Surface((WIDTH, WINDOW_HEIGHT), pygame.SRCALPHA)
        overlay.fill((0, 0, 0, 140))
        window.blit(overlay, (0, 0))

        msg = FONT.render("Game Over", True, (255, 255, 255))
        window.blit(
            msg,
            (WIDTH // 2 - msg.get_width() // 2, BOARD_Y + HEIGHT // 2 - msg.get_height() // 2),
        )

    pygame.display.update()


# -----------------------------
# Helpers: choosing empty cells
# -----------------------------

#TODO
def get_random_pos(tiles):
    """Return a random (row, col) from empty cells, or None if full."""

    return None


def update_tiles(window, tiles, tile_list, score):
    """
    Rebuild the 2D grid from a list of Tile objects, then redraw.
    (During animation, tiles can be "between" cells; we treat tile.row/tile.col as source of truth.)
    """
    for r in range(ROW):
        for c in range(COL):
            tiles[r][c] = None

    for tile in tile_list:
        tiles[tile.row][tile.col] = tile

    draw(window, tiles, score)

#TODO
def end_tiles(tiles):
    """
    After a successful move finishes animating:
      - If board is full: return "Game Over"
      - Else: spawn a new tile (2 or 4) in a random empty spot
    """


    # No empty spaces: only game over if no merges are possible

    return


# -----------------------------
# Sorting keys for processing
# -----------------------------
def sortfunc_col(tile):
    """Sort tiles by column index."""
    return tile.col


def sortfunc_row(tile):
    """Sort tiles by row index."""
    return tile.row


# -----------------------------
# Boundary checks (can't move further)
# -----------------------------
def bounds_left(tile):   return tile.col == 0
def bounds_right(tile):  return tile.col == COL - 1
def bounds_up(tile):     return tile.row == 0
def bounds_down(tile):   return tile.row == ROW - 1


# -----------------------------
# Neighbor lookup (adjacent cell in the move direction)
# -----------------------------
def next_left(tile, tiles):   return tiles[tile.row][tile.col - 1]
def next_right(tile, tiles):  return tiles[tile.row][tile.col + 1]
def next_up(tile, tiles):     return tiles[tile.row - 1][tile.col]
def next_down(tile, tiles):   return tiles[tile.row + 1][tile.col]


# -----------------------------
# Animation checks
#   - merge_check_*: keep moving until tile reaches the merge position
#   - move_check_*: keep moving through gaps until tile is adjacent
# -----------------------------
def merge_check_left(tile, next_tile):   return tile.x > next_tile.x + VEL
def merge_check_right(tile, next_tile):  return tile.x < next_tile.x - VEL
def merge_check_up(tile, next_tile):     return tile.y > next_tile.y + VEL
def merge_check_down(tile, next_tile):   return tile.y < next_tile.y - VEL

def move_check_left(tile, next_tile):    return tile.x > next_tile.x + RECT_WIDTH + VEL
def move_check_right(tile, next_tile):   return tile.x + RECT_WIDTH + VEL < next_tile.x
def move_check_up(tile, next_tile):      return tile.y > next_tile.y + RECT_HEIGHT + VEL
def move_check_down(tile, next_tile):    return tile.y + RECT_HEIGHT + VEL < next_tile.y


# -----------------------------
# Core movement + merge + animation
# -----------------------------
def move_tiles(window, tiles, clock, direction, score):
    """
    Animate a single move in a given direction:
      - slide tiles through empty space
      - merge equal adjacent tiles (once per tile per move)
      - update tile.row/tile.col while animating
      - spawn a new tile at the end
      - add to score if merged
    """
    gained = 0

    # Tracks tiles that already merged this move (prevents double-merge in one swipe).
    merged_this_move = set()

    # Pick direction-specific behavior.
    if direction == "left":
        sortfunc = sortfunc_col
        reverse = False
        movediff = (-VEL, 0)
        at_boundary = bounds_left
        neighbor = next_left
        merge_check = merge_check_left
        gap_check = move_check_left
        round_up = True

    elif direction == "right":
        sortfunc = sortfunc_col
        reverse = True
        movediff = (VEL, 0)
        at_boundary = bounds_right
        neighbor = next_right
        merge_check = merge_check_right
        gap_check = move_check_right
        round_up = False

    elif direction == "up":
        sortfunc = sortfunc_row
        reverse = False
        movediff = (0, -VEL)
        at_boundary = bounds_up
        neighbor = next_up
        merge_check = merge_check_up
        gap_check = move_check_up
        round_up = True

    elif direction == "down":
        sortfunc = sortfunc_row
        reverse = True
        movediff = (0, VEL)
        at_boundary = bounds_down
        neighbor = next_down
        merge_check = merge_check_down
        gap_check = move_check_down
        round_up = False

    else:
        # Invalid direction: no move, no score gained
        return "continue", 0

    # Repeat animation frames until no tile moved/merged on a frame.
    updated = True
    while updated:
        clock.tick(FPS)
        updated = False

        # Process tiles in the correct order for the direction.
        active_tiles = sorted(
            [tiles[r][c] for r in range(ROW) for c in range(COL) if tiles[r][c] is not None],
            key=sortfunc,
            reverse=reverse,
        )

        for i, tile in enumerate(active_tiles):
            if at_boundary(tile):
                continue

            next_tile = neighbor(tile, tiles)

            # Case 1: empty cell ahead -> slide
            if next_tile is None:
                tile.move(movediff)

            # Case 2: same value and neither has merged yet -> merge
            elif (
                tile.value == next_tile.value
                and tile not in merged_this_move
                and next_tile not in merged_this_move
            ):
                # Keep moving until we reach the merge position, then merge.
                if merge_check(tile, next_tile):
                    tile.move(movediff)
                else:
                    next_tile.value *= 2
                    gained += next_tile.value
                    next_tile.update_image()
                    active_tiles.pop(i)           # Remove current tile
                    merged_this_move.add(next_tile)

            # Case 3: different value ahead, but there is a gap -> slide into the gap
            elif gap_check(tile, next_tile):
                tile.move(movediff)

            # Case 4: blocked -> do nothing
            else:
                continue

            tile.set_pos(round_up)
            updated = True

        # Pass score + gained so HUD updates during animation
        update_tiles(window, tiles, active_tiles, score + gained)

    # Snap tiles to exact grid pixels so arbitrary WIDTH/HEIGHT/VEL still align cleanly.
    for r in range(ROW):
        for c in range(COL):
            t = tiles[r][c]
            if t is not None:
                t.x = t.col * RECT_WIDTH
                t.y = t.row * RECT_HEIGHT

    # Redraw once in the snapped final state
    draw(window, tiles, score + gained)

    return end_tiles(tiles), gained


# -----------------------------
# Game setup + main loop
# -----------------------------

#TODO
def generate_tiles():
    """Start game with two '2' tiles in random positions."""

    tiles = create_board()
    return tiles


def main(window):
    clock = pygame.time.Clock()
    tiles = generate_tiles()

    score = 0
    game_over = False

    run = True
    while run:
        clock.tick(FPS)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False
                break

            if event.type == pygame.KEYDOWN and not game_over:
                direction = None
                if event.key == pygame.K_LEFT:
                    direction = "left"
                #TODO: complete this for all 4 directions

                if direction:
                    status, gained = move_tiles(window, tiles, clock, direction, score)
                    score += gained

                    # Drop any key presses that happened during the animation
                    pygame.event.clear(pygame.KEYDOWN)

                    if status == "Game Over":
                        game_over = True

        draw(window, tiles, score, game_over)

    pygame.quit()


if __name__ == "__main__":
    main(WINDOW)
