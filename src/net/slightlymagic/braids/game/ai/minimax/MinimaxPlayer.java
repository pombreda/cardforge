package net.slightlymagic.braids.game.ai.minimax;

/**
 * This represents a player on a team.
 * 
 * If your game does not support team-play, then your class may implement both
 * MinimaxPlayer and MinimaxTeam.
 */
public interface MinimaxPlayer {
	/**
	 * @return the MinimaxTeam of which this player is a member.
	 */
	MinimaxTeam getTeam();
}
