#include "Pawn.h"


Pawn::Pawn(Team team, int row, int col) : m_team(team), m_row(row), m_col(col)
{
	m_str = (team == Team::white) ? "p" : "P";
}

virtual bool Pawn::move()
{
	return true;
}

virtual bool Pawn::isValidMove()
{
	return true;
}