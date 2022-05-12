import de.hglabor.youtuberideen.game.AbstractGamePhase

interface IGamePhaseManager {
   var phase: AbstractGamePhase
   fun resetTimer()
}