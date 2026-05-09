package BrotatoBuildPlanner.Vista.Renderer;

import BrotatoBuildPlanner.Modelo.Item.Items;
import java.util.function.Function;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

/**
 * Utilidades para celdas de listas JavaFX.
 */
public final class Renderer {

    private Renderer() {
    }

    public static <T extends Items> ListCell<T> namedCell(Function<T, String> tooltipFormatter) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                setText(item.getName());
                if (tooltipFormatter != null) {
                    setTooltip(new Tooltip(tooltipFormatter.apply(item)));
                }
            }
        };
    }
}
