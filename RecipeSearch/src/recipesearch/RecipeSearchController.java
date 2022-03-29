
package recipesearch;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;


public class RecipeSearchController implements Initializable {
    ToggleGroup difficultyToggleGroup;
    RecipeDatabase db = RecipeDatabase.getSharedInstance();
    @FXML
    private FlowPane recipeList;
    @FXML
    private ComboBox<String> ingredient;
    @FXML
    private ComboBox<String> cuisine;
    @FXML
    private RadioButton allDifficulty;
    @FXML
    private RadioButton easyDifficulty;
    @FXML
    private RadioButton betweenDifficulty;
    @FXML
    private RadioButton hardDifficulty;
    @FXML
    private Spinner<Integer> maxPrice;
    @FXML
    private Slider maxTime;
    @FXML
    private Label labelTime;
    @FXML
    private Label detailedViewLabel;
    @FXML
    private ImageView detailedViewImage;
    @FXML
    private SplitPane searchPane;
    @FXML
    private AnchorPane recipeDetailPane;
    @FXML
    private Button closeBtn;
    private Map<String, RecipeListItem> recipeListItemMap = new HashMap<String, RecipeListItem>();



    SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 150, 0, 1);

    private RecipeBackendController backendControll = new RecipeBackendController();
    private List<Recipe> listRecipe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ingredient.getItems().addAll("Visa alla", "Kött", "Fisk", "Kyckling", "Vegetarisk");
        ingredient.getSelectionModel().select("Visa alla");
        ingredient.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                backendControll.setMainIngredient(newValue);
                updateRecipeList();
            }
        });

        cuisine.getItems().addAll("Visa alla", "Sverige", "Grekland", "Indien", "Asien", "Afrika", "Frankrike");
        cuisine.getSelectionModel().select("Visa alla");
        cuisine.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                backendControll.setCuisine(newValue);
                updateRecipeList();
            }
        });

        difficultyToggleGroup = new ToggleGroup();

        allDifficulty.setSelected(true);
        allDifficulty.setToggleGroup(difficultyToggleGroup);
        easyDifficulty.setToggleGroup(difficultyToggleGroup);
        betweenDifficulty.setToggleGroup(difficultyToggleGroup);
        hardDifficulty.setToggleGroup(difficultyToggleGroup);

        difficultyToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

                if (difficultyToggleGroup.getSelectedToggle() != null) {
                    RadioButton selected = (RadioButton) difficultyToggleGroup.getSelectedToggle();
                    backendControll.setDifficulty(selected.getText());
                    updateRecipeList();
                }
            }
        });

        maxPrice.setValueFactory(valueFactory);
        maxPrice.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                if (maxPrice.getValue() != null) {
                    Integer selected = maxPrice.getValue();
                    backendControll.setMaxPrice(selected);
                    updateRecipeList();
                }
            }
        });

        maxPrice.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if(newValue){
                    //focusgained - do nothing
                }
                else{
                    Integer value = Integer.valueOf(maxPrice.getEditor().getText());
                    backendControll.setMaxPrice(value);
                    updateRecipeList();
                }

            }
        });

        maxTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if(newValue != null && !newValue.equals(oldValue)) {
                    backendControll.setMaxTime((int)Math.rint(newValue.intValue() /10) * 10);
                    updateRecipeList();
                }

                labelTime.setText(String.valueOf((int)Math.rint(newValue.intValue() /10) * 10) + " minuter");
            }});

        for (Recipe recipe : backendControll.getRecipes()) {
            RecipeListItem recipeListItem = new RecipeListItem(recipe, this);
            recipeListItemMap.put(recipe.getName(), recipeListItem);
        }

        updateRecipeList();
    }

    private void updateRecipeList(){
        recipeList.getChildren().clear();
        listRecipe = backendControll.getRecipes();

        for(Recipe recipe: listRecipe){
            RecipeListItem listItem = recipeListItemMap.get(recipe.getName());
            recipeList.getChildren().add(listItem);
        }
    }

    private void populateRecipeDetailView(Recipe recipe){
        detailedViewLabel.setText(recipe.getName());
        detailedViewImage.setImage(recipe.getFXImage());
    }

    @FXML
    public void closeRecipeView(){
        searchPane.toFront();
    }

    public void openRecipeView(Recipe recipe){
        populateRecipeDetailView(recipe);
        recipeDetailPane.toFront();
    }


}