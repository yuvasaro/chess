#include "Board.h"
#include "globals.h"

Board::Board() : m_rows(DEFAULT_SIZE), m_cols(DEFAULT_SIZE)
{
	for (int i = 0; i < DEFAULT_SIZE; i++)
		for (int j = 0; j < DEFAULT_SIZE; j++)
		{
			m_pieces[i][j] = nullptr;
		}
}

Board::~Board()
{

}