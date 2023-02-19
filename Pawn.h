#ifndef PAWN_H
#define PAWN_H

#include "Piece.h"

class Pawn : public Piece
{
public:
	Pawn(Team team, int row, int col); 

	virtual bool move();
	virtual bool isValidMove();

private:
	
};

#endif