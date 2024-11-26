package net.robertx.planeteze_b07.annual_carbon_footprint;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import net.robertx.planeteze_b07.R;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonSystem extends LinearLayout {

    private final List<ToggleButton> toggleButtons = new ArrayList<>();

    public RadioButtonSystem(Context context) {
        super(context);
        setOrientation(VERTICAL);


    }

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


        toggleButton.setOnClickListener(onToggleClickListener);
        toggleButtons.add(toggleButton);
        addView(toggleButton);
    }

    public void setDefaultOption(int index) {
        if (index >= 0 && index < toggleButtons.size()) {
            toggleButtons.get(index).setChecked(true);
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

    private final OnClickListener onToggleClickListener = view -> {
        ToggleButton clickedButton = (ToggleButton) view;
        for (ToggleButton button : toggleButtons) {
            if (button != clickedButton) {
                button.setChecked(false);
            }
        }
    };

}
