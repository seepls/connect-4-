package com.smriti.connect;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import static com.smriti.connect.Controller.insertDisc;

public class Controller implements Initializable {


    private  static final int COLUMNS = 7;
    private  static final int ROWS = 6 ;
    private  static final int CIRCLE_DIAMETER = 80 ;
    private static final String discColor1 = "RED";
    private static final String discColor2 = "BLUE";

    private   String PLAYER_ONE ;
    private   String PLAYER_TWO ;


    private boolean isPlayeroneturn = true;

    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];





    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public Label playerNameLabel;


    @FXML
    public TextField playerOneTextField , playerTwoTextField ;

    @FXML
    public Button setNamesButton ;






    private  boolean isAllowedToInsert  = true ; // flag

    public void createPlayground(){
        Shape rectangleWithHoles = createGameStructuralGrid();
        setNamesButton.setOnAction(event->{
               PLAYER_ONE= playerOneTextField.getText();
               PLAYER_TWO = playerTwoTextField.getText();
            //todo
        });

        rootGridPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangleList = createClickableColumns();
        for (Rectangle rectangle :rectangleList) {
            rootGridPane.add(rectangle,0,1 );
            
        }


    }
    private  Shape createGameStructuralGrid(){
        Shape rectangleWithHoles =  new Rectangle((COLUMNS+1) * CIRCLE_DIAMETER , (ROWS+1)*CIRCLE_DIAMETER);



        // nested for loop needed to subtract the circles from the rectangle to display the background color
        for(int row = 0; row < ROWS; row++)
        {
            for(int col=0 ; col < COLUMNS ; col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);

                circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5) +CIRCLE_DIAMETER/4);


                rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
            }
        }


        rectangleWithHoles.setFill(Color.WHITE);
      return rectangleWithHoles ;


    }


    private List<Rectangle> createClickableColumns(){

        // this method will create columns to be able to insert disc from the top
        //need to create list of rectangle objects that will hold all the rectangles created
        List<Rectangle> rectangleList = new ArrayList<>();

        for(int col = 0 ; col < COLUMNS;col++){
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);


            //adding the hower effect
            rectangle.setOnMouseEntered(event->rectangle.setFill(Color.valueOf("#00ff0010")));
            rectangle.setOnMouseExited(event->rectangle.setFill(Color.TRANSPARENT));
            final int column = col;
            rectangle.setOnMouseClicked(event-> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false;
                    insertDisc(new Disc(isPlayeroneturn), column);
                }

                });


            rectangleList.add(rectangle);

        }




        return rectangleList;

    }


    private  void insertDisc(Disc disc,int column ){

        int row = ROWS - 1;
        while(row>=0){
            if(getDiscIfPresent(row,column)==null){
                break;
            }row--;

        }
        if(row<0)return;

        insertedDiscsArray[row][column]=disc;
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

        int currentRow  = row;

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);

        translateTransition.setToY(row*(CIRCLE_DIAMETER+5) +CIRCLE_DIAMETER/4);
        translateTransition.setOnFinished(event ->{
            isAllowedToInsert = true ;

            if (gameEnded (currentRow, column)){
                gameover();
                return ;
            }


            isPlayeroneturn = !isPlayeroneturn;


            playerNameLabel.setText(isPlayeroneturn? PLAYER_ONE:PLAYER_TWO);
        });
        translateTransition.play();

    }


    private boolean gameEnded(int row , int column){
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3). // gets range of row values
                                        mapToObj(r->new Point2D(r,column)).
                                        collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column+3). // gets range of row values
                mapToObj(c->new Point2D(row,c)).
                collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row-3,column+3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).
                mapToObj(i->startPoint1.add(i,-i)).
                collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row-3,column- 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).
                    mapToObj(i->startPoint2.add(i,i)).
                collect(Collectors.toList());



        boolean isEnded = checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)
                ||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);


        return isEnded ;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;
        for (Point2D point:points ) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if(disc!= null && disc.isPlayeroneMove == isPlayeroneturn){// if the last inserted disc belongs to the current player
                chain++;
                if (chain == 4){
                    return true ;   // player 1 move and turn
                }

            }else {
                chain = 0;
            }
        }


        return false ;

    }


    private  Disc getDiscIfPresent(int row , int column){
        if(row >= ROWS || row <0||column >= COLUMNS||column <0)
            return  null ;
        return insertedDiscsArray[row][column];

        }

    private void gameover(){
        String winner = isPlayeroneturn?PLAYER_ONE : PLAYER_TWO ;
        System.out.println("winner is : "+ winner );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GAME OVER");
        alert.setHeaderText("winner is : "+ winner);
        alert.setContentText(" wanna play again ? ");
        ButtonType yesButton  = new ButtonType("YES! :) ");
        ButtonType noButton  = new ButtonType("NO! :( ");
        alert.getButtonTypes().setAll(yesButton,noButton);


        Platform.runLater(()->{
            Optional<ButtonType> btnclicked = alert.showAndWait();
            if(btnclicked.isPresent() && btnclicked.get()==yesButton){
                resetgame();
            }else{
                Platform.exit();
                System.exit(0);

            }
        });


    }

    public  void resetgame() {
        insertedDiscsPane.getChildren().clear();
        for (int row  = 0; row  < insertedDiscsArray.length; row ++) {
            for (int col = 0; col < insertedDiscsArray[row].length; col++) {
                insertedDiscsArray[row][col] = null;

            }
        }
        isPlayeroneturn = true ;
        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();
    }


    private  static class Disc  extends Circle{
        // THIS METHOD helps you to determine the color of the disc
        private final boolean isPlayeroneMove ;
        public Disc(boolean isPlayeroneMove){
            this.isPlayeroneMove = isPlayeroneMove;

                setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayeroneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);

        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
