package net.robertx.planeteze_b07.ui.dashboard;

import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import net.robertx.planeteze_b07.dailySurvey.CalendarPage;
import net.robertx.planeteze_b07.dailySurvey.DailySurveyHomePage;
import net.robertx.planeteze_b07.dailySurvey.QuestionList;
import net.robertx.planeteze_b07.dailySurvey.QuestionnairePageQ1;
import net.robertx.planeteze_b07.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Fragment for displaying past survey results.
 *
 * This class provides a calendar view that highlights the days when surveys were taken
 * and allows users to navigate to detailed results or the survey home page. It integrates
 * with Firebase Realtime Database and Authentication to fetch and manage user-specific survey data.
 */
public class PastSurveyResultsFragment extends Fragment {

    /**
     * Instance of Firebase Realtime Database used for accessing and managing
     * user-specific survey data.
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Instance of Firebase Authentication used for managing user authentication
     * and retrieving the current authenticated user.
     */
    final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Initializes the view for the Past Survey Results fragment.
     *
     * This method inflates the layout for the fragment, setting up the user interface
     * for displaying the calendar and survey results.
     *
     * @param inflater the {@link LayoutInflater} used to inflate the fragment's layout.
     * @param container the parent {@link ViewGroup} that the fragment's UI will be attached to, or null.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state, or null if none.
     * @return the root {@link View} for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_survey_results, container, false);
    }

    /**
     * Sets up the calendar view and survey data for the Past Survey Results fragment.
     *
     * This method initializes the calendar with a range of months, binds day and month
     * views with custom functionality, and fetches user-specific survey data from Firebase.
     * The calendar highlights the days on which surveys were completed and allows navigation
     * to detailed survey results or the survey home page based on user interactions.
     *
     * @param view the root {@link View} for the fragment's UI.
     * @param savedInstanceState a {@link Bundle} containing the fragment's previously saved state, or null if none.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Set<LocalDate> daysWithSurveyTaken = new HashSet<>();

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(6);
        YearMonth endMonth = currentMonth.plusMonths(6);

        DayOfWeek firstDayOfWeek = firstDayOfWeekFromLocale();
        calendarView.setup(startMonth, endMonth, firstDayOfWeek);

        calendarView.setDayBinder(new MonthDayBinder<>() {

            @Override
            public void bind(@NonNull ViewContainer container, CalendarDay calendarDay) {
                container.getView().setOnClickListener(v -> {
                    LocalDate date = calendarDay.getDate();
                    // extremely hacky code to go to the correct day for the Question List
                    // or daily survey home page because of poor design
                    QuestionnairePageQ1.ChangedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    CalendarPage.datedisplay = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    CalendarPage.SelectedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    CalendarPage.ChosenDay = String.valueOf(date.getDayOfMonth());
                    CalendarPage.ChosenMonth = String.valueOf(date.getMonthValue());
                    CalendarPage.ChosenYear = String.valueOf(date.getYear());

                    if (daysWithSurveyTaken.contains(date)) {
                        Intent intent = new Intent(getContext(), QuestionList.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), DailySurveyHomePage.class);
                        startActivity(intent);
                    }
                });
                TextView dayText = container.getView().findViewById(R.id.calendarDayText);
                View dayDot = container.getView().findViewById(R.id.dayDot);
                dayDot.setVisibility(View.VISIBLE);
                LocalDate date = calendarDay.getDate();
                dayText.setText(String.valueOf(date.getDayOfMonth()));

                if (date.equals(LocalDate.now())) {
                    dayText.setTextColor(getResources().getColor(R.color.teal, getContext().getTheme()));
                    dayText.setTypeface(null, android.graphics.Typeface.BOLD);
                }

                if (daysWithSurveyTaken.contains(date)) {
                    dayDot.setVisibility(View.VISIBLE);
                } else {
                    dayDot.setVisibility(View.INVISIBLE);
                }

                if (calendarDay.getPosition() != DayPosition.MonthDate) {
                    dayText.setVisibility(View.INVISIBLE);
                    dayDot.setVisibility(View.INVISIBLE);
                } else {
                    dayText.setVisibility(View.VISIBLE);
                }
            }

            @NonNull
            @Override
            public ViewContainer create(@NonNull View view) {
                return new ViewContainer(view);
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<>() {
            @Override
            public void bind(@NonNull ViewContainer container, CalendarMonth calendarMonth) {
                TextView textView = container.getView().findViewById(R.id.calendar_month_text);
                // get Locale
                Locale locale = getResources().getConfiguration().getLocales().get(0);
                textView.setText(String.format(locale, "%s %d", calendarMonth.getYearMonth().getMonth().name(), calendarMonth.getYearMonth().getYear()));
            }

            @NonNull
            @Override
            public ViewContainer create(@NonNull View view) {
                return new ViewContainer(view);
            }

        });

        calendarView.scrollToMonth(currentMonth);

        if (auth.getCurrentUser() != null) {
            DatabaseReference surveyDaysDoneDbRef = database.getReference("DailySurvey").child(auth.getCurrentUser().getUid());
            surveyDaysDoneDbRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DataSnapshot day : task.getResult().getChildren()) {
                        LocalDate date = LocalDate.parse(day.getKey());
                        daysWithSurveyTaken.add(date);
                        calendarView.notifyDateChanged(date);
                    }
                }
            });
        }


    }
}
