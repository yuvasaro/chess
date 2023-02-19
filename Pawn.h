#ifndef PAWN_H
#define PAWN_H

#include "Piece.h"

class Pawn : public Piece
{
public:
	Pawn(Team team, int row, int col) {
		m_team = team;
		m_str = team == Team::white ? "p" : "P";
		m_row = row;
		m_col = col;
	}

	virtual bool move()
	{

	}

private:
	
};

#endif