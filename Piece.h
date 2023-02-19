#ifndef PIECE_H
#define PIECE_H

#include <utility>
#include <string>

class Player;

class Piece
{
public:
	Piece() {}
	~Piece() {}

	virtual bool move();		

	std::string toString() const { return m_str; }
	char getTeam() const { return m_team; } 

private:
	int m_row;
	std::string m_str;
	char m_team;
	//	int m_value; replace with hash tables
	Player* m_p1;
	Player* m_p2;
};

#endif