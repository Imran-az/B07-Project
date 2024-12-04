package net.robertx.planeteze_b07.annual_carbon_footprint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view for creating a radio button system using {@link ToggleButton}.
 *
 * This class provides a flexible layout for adding multiple toggle buttons
 * as options, allowing only one option to be selected at a time. It includes
 * methods to add options, set default selections, clear selections, and retrieve
 * the selected option's index.
 */
public class RadioButtonSystem extends LinearLayout {

    /**
     * A list of {@link ToggleButton} instances representing the selectable options
     * in the radio button system. Each button corresponds to a single option, and
     * only one button can be selected at a time.
     */
    private final List<ToggleButton> toggleButtons = new ArrayList<>();

    /**
     * Constructs a {@code RadioButtonSystem} with the specified context.
     *
     * This constructor initializes the layout and sets the orientation to vertical.
     *
     * @param context the {@link Context} in which the radio button system is created.
     */
    public RadioButtonSystem(Context context) {
        super(context);
        setOrientation(VERTICAL);


    }

    /**
     * Constructs a {@code RadioButtonSystem} with the specified context and attributes.
     *
     * This constructor initializes the layout with the provided context and attribute set,
     * and sets the orientation to vertical.
     *
     * @param context the {@link Context} in which the radio button system is created.
     * @param attrs the {@link AttributeSet} containing custom attributes for the radio button system.
     */
    public RadioButtonSystem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    /**
     * Add a list of options
     * @param labels A list of option labels to add
     */
    public void addOptions(List<String> labels) {
        for (String option: labels) {
            addOption(option);
        }
    }

    /**
     * Add a selectable option
     *
     * @param label The text to display for the option.
     */
    public void addOption(String label) {
        ToggleButton toggleButton = new ToggleButton(getContext());
        toggleButton.setTextOn(label);
        toggleButton.setTextOff(label);
        toggleButton.setText(label);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 16);
        toggleButton.setLayoutParams(params);


        toggleButton.setBackgroundResource(R.drawable.toggle_button_background);


//        toggleButton.setOnClickListener(onToggleClickListener);
        toggleButton.setOnCheckedChangeListener(onCheckedChangeListener);
        toggleButtons.add(toggleButton);
        addView(toggleButton);
    }

    /**
     * Sets the default selected option in the radio button system.
     *
     * This method selects the toggle button at the specified index, marks it as checked,
     * and disables it to indicate that it is the default selection.
     *
     * @param index the index of the toggle button to set as the default option.
     *              Must be within the bounds of the {@code toggleButtons} list.
     */
    public void setDefaultOption(int index) {
        if (index >= 0 && index < toggleButtons.size()) {
            toggleButtons.get(index).setChecked(true);
            toggleButtons.get(index).setEnabled(false);
        }
    }

    /**
     * Clear the selection of all options
     */
    public void clearSelection() {
        for (ToggleButton button : toggleButtons) {
            button.setChecked(false);
        }
    }

    /**
     * Get the index of the currently selected option
     * @return The index of the selected option, or -1 if no option is selected.
     */
    public int getSelectedIndex() {
        for (int i = 0; i < toggleButtons.size(); i++) {
            if (toggleButtons.get(i).isChecked()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Listener for handling toggle button state changes.
     *
     * This listener ensures that only one toggle button is selected at a time. When a button is
     * checked, it deselects all other buttons, resets their background tint, and enables them.
     * The selected button is highlighted with a specific tint and disabled to indicate selection.
     */
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
        if (isChecked) {
            for (ToggleButton button : toggleButtons) {
                if (button != buttonView) {
                    button.setChecked(false);
                    button.setEnabled(true);
                    // Reset unselected buttons to default (no tint applied)
                    button.setBackgroundTintList(null);
                }
            }
            // Highlight the selected button with lighter teal
            buttonView.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.highlight_teal))
            );
            buttonView.setEnabled(false);
        } else {
            // Reset the button back to default if deselected
            buttonView.setBackgroundTintList(null);
        }
    };

    /**
     * Listener for handling toggle button clicks.
     *
     * This listener ensures that the clicked button is not disabled if it is already checked.
     * It deselects and enables all other buttons in the group while disabling the clicked button
     * to maintain the radio button behavior.
     */
    private final OnClickListener onToggleClickListener = view -> {
        ToggleButton clickedButton = (ToggleButton) view;
        // prevent disabling the clicked button if it is already checked
        if (clickedButton.isChecked()) {
            return;
        }

        for (ToggleButton button : toggleButtons) {
            if (button != clickedButton) {
                button.setEnabled(true);
                button.setChecked(false);
            }
        }
        clickedButton.setEnabled(false);
    };

}
