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

	//	Pure virtual 
	virtual bool move() = 0;
	virtual bool isValidMove() = 0;		

	//	Getters
	std::string toString() const { return m_str; }
	Team getTeam() const { return m_team; }

protected:
	std::string m_str;
	Team m_team;
	Player* m_p1;
	Player* m_p2;
	int m_row;
	int m_col;
	//	int m_value; replace with hash tables
};

#endif