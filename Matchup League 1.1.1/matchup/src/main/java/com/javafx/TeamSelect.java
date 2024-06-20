package com.javafx;

import java.io.IOException;
import com.Entities.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TeamSelect extends MenuHandler
{
    private League selectedLeague;
    private Team selectedTeam;
    @FXML private ChoiceBox<String> leagues;
    @FXML private ChoiceBox<String> teams;
    @FXML private Label fans, errorLabel, debug;
    @FXML private TextField name;
     
    @FXML
    public void initialize() {
        for (League lg : leagueList) {
            leagues.getItems().add(lg.getName()); 
        }
        leagueListener();
        teamListener();
    }

    private void leagueListener()
    {
        leagues.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
            {
                if (newVal.intValue() >= 0)
                {
                    selectedLeague = leagueList.get(newVal.intValue());

                    fans.setText("");
                    teams.getItems().clear();
                    for (Team t : selectedLeague.getTeamList())
                        teams.getItems().add(t.getName()); 
                }
                else System.out.println("league choice bar index is less than 0");

            }
        });
    }

    private void teamListener()
    {
        teams.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal)
            {
                if (newVal.intValue() >= 0)
                {
                    selectedTeam = selectedLeague.getTeamList().get(newVal.intValue());
                    fans.setText("Fans: " + selectedTeam.getFans());
                }  
                else System.out.println("team choice bar index is less than 0");
            }
        });
    }

    @FXML
    private void startSeason() throws IOException 
    {
        if (name.getText().isEmpty() || selectedTeam == null) {
            errorLabel.setText("Enter a name and select a team before continuing");
            //initialize();
        } else if (menuManager.addUser(name.getText(), selectedTeam)) {
            
            long extraFighters = menuRepo.extraFighters(menuManager.getFPT());
            boolean eligibleStart = true;
            // If extra fighters are needed, generates just enough to fill each team.
            if (extraFighters < 0) {
                if (!menuManager.generateGeneric((extraFighters * -1) + menuManager.getFPT())) {
                    errorLabel.setText("An error generating fighters occured, try again");
                    eligibleStart = false;
                }
            }
            if (eligibleStart) {
                setUserTeam(selectedTeam);
                for (League lg : leagueList)
                    menuManager.generateSchedule(lg);
                App.setRoot("draft_menu");
            }
        }
        else {
            errorLabel.setText("An error occured, try again");
            initialize();
        }
    }

    @FXML private void toMenu() throws IOException {super.toMainMenu();}
}
