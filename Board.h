#ifndef BOARD_H
#define BOARD_H

#include "globals.h"

class Piece;

//	Test
class Board
{
public:
	Board() : m_rows(DEFAULT_SIZE), m_cols(DEFAULT_SIZE)
	{
		for (int i = 0; i < DEFAULT_SIZE; i++)
			for (int j = 0; j < DEFAULT_SIZE; j++)
			{
				m_pieces[i][j] = nullptr;
			}
	}
	~Board();	//	will need implementation

private:
	int m_rows;
	int m_cols;
	Piece* m_pieces[DEFAULT_SIZE][DEFAULT_SIZE] = {};
	Piece* m_piece;
};

#endif