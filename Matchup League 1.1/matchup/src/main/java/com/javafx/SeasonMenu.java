package com.javafx;

import java.io.IOException;
import com.Entities.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SeasonMenu extends MenuHandler
{
    private League selectedLeague;
    private int selectedRound;
    private int userRank;
    @FXML private Label teamLabel;
    @FXML private Label roundLabel;
    @FXML private Label nextLabel;
    @FXML private Label recordLabel;
    @FXML private ChoiceBox<String> leagueChoice;
    @FXML private ChoiceBox<String> gameChoice;
    @FXML private VBox standingsBox;
    @FXML private VBox gameBox;
    
    @FXML
    public void initialize()
    {
        selectedLeague = userLeague;
        selectedRound = round;
        setLeague(selectedLeague);
        
        teamLabel.setText(userTeam.getName());
        recordLabel.setText("Record: " + userTeam.getWins() + "-" + userTeam.getLosses());
        if (round >= userLeague.getGameAmt()) {
            roundLabel.setText("Season over");
            nextLabel.setText("Congrats! You finished " + App.addSuffix(userRank));
        }
        else {
            roundLabel.setText("Game " + (round + 1) + "/" + userLeague.getGameAmt());
            nextLabel.setText("Next game: vs " + userTeam.getOpponent(round).getName());
        }
    
        leagueListener();
        gameListener();
    }

    private void leagueListener()
    {
        for (League lg : leagueList) {
            leagueChoice.getItems().add(lg.getName()); 
        }
        leagueChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
            {
                selectedLeague = leagueList.get(newVal.intValue());
                if (selectedLeague != null)
                {
                    setLeague(selectedLeague);
                }
                else System.out.println("league choice bar index is less than 0");
            }
        });
    }

    private void gameListener()
    {
        gameChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
            {
                selectedRound = newVal.intValue();
                if (selectedRound >= 0)
                {
                    setGames(selectedRound, selectedLeague);
                }
                else System.out.println("game choice bar index is less than 0");
            }
        });
    }

    private void setLeague(League lg)
    {
        setStandings(lg);
        gameChoice.getItems().clear();
        for (int i = 1; i <= lg.getGameAmt(); i++)
            gameChoice.getItems().add("Game " + i);
        setGames(selectedRound, lg);
    }

    private void setStandings(League lg)
    {
        standingsBox.getChildren().clear(); 
        int rank = 1;
        for (Team t : menuRepo.getStandings(lg))
        {
            if (t == userTeam)
                userRank = rank;
            if (t.getFans() != -1)
                standingsBox.getChildren().add(new Label(rank++ + ". " + t.getName() + " " + t.getWins() + "-" + t.getLosses()));
        }
    }

    private void setGames(int round, League lg)
    {
        gameBox.getChildren().clear();
        for (Game g : menuRepo.getGames_inLeague_byRound(round, lg))
        {
            gameBox.getChildren().add(new Label(g.getGameString()));
        }
    }

    @FXML private void startGame() throws IOException 
    {
        if (round < userLeague.getGameAmt()) {
            if (userTeam.onBye(round))
                menuManager.simRound(round++);
            else
                App.setRoot("game_menu");
        }
        else {
            while (!allLeaguesFinished())
                menuManager.simRound(round++);
        }
    }
    @FXML private void toTeamView() throws IOException {App.setRoot("team_view");}
    @FXML private void toMenu() throws IOException {super.toMainMenu();}
}