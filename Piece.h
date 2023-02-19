#ifndef PIECE_H
#define PIECE_H

#include <utility>
#include <string>

class Player;

enum Team { white, black };

class Piece
{
public:
	Piece() {}
	~Piece() {}

	virtual bool move();
	virtual bool isValidMove();		

	std::string toString() const { return m_str; }
	Team getTeam() const { return m_team; }

protected:
	std::string m_str;
	Team m_team;
	//	int m_value; replace with hash tables
	Player* m_p1;
	Player* m_p2;
	int m_row;
	int m_col;
};

#endif