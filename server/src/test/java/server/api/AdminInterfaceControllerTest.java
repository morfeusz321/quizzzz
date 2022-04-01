package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import server.database.ActivityDB;
import server.database.ActivityDBController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminInterfaceController.class)
class AdminInterfaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityDBController activityDBController;


    @Test
    void getAllActivities() throws Exception {
        List<Activity> list = new ArrayList<>();
        list.add(new Activity("1", "/path/to/image/", "Activity 1", 9));
        list.add( new Activity("2", "/path/to/image/", "Activity 2", 10));
        list.add( new Activity("3", "/path/to/image/", "Activity 3", 11));
        list.add( new Activity("4", "/path/to/image/", "Activity 4", 999999999));
        Mockito.when(activityDBController.listAll()).thenReturn(list);
        String url = "/debug/activities";
        mockMvc.perform(get(url)).andExpect(status().isOk());
        verify(activityDBController,times(1)).listAll();
    }

    @Test
    void edit() throws Exception {
        ObjectMapper objectMapper =new ObjectMapper();
        ActivityDB activityDB =mock(ActivityDB.class);
        Activity activity = new Activity("1", "/path/to/image/", "Activity 1", 9);
        Mockito.when(activityDBController.getInternalDB()).thenReturn(activityDB);
        Mockito.when(activityDB.save(activity)).thenReturn(activity);
        String url = "/debug/activities/edit";
        mockMvc.perform(post(url).content(objectMapper.writeValueAsString(activity)).contentType("application/json")).andExpect(status().isOk());
        verify(activityDB,times(1)).save(activity);
        verify(activityDBController,times(1)).getInternalDB();

    }

    @Test
    void delete() throws Exception {
        ObjectMapper objectMapper =new ObjectMapper();
        ActivityDB activityDB =mock(ActivityDB.class);
        Activity activity = new Activity("1", "/path/to/image/", "Activity 1", 9);
        Mockito.when(activityDBController.getInternalDB()).thenReturn(activityDB);
        String url = "/debug/activities/delete";
        mockMvc.perform(post(url).content(objectMapper.writeValueAsString(activity)).contentType("application/json")).andExpect(status().isOk());
        verify(activityDB,times(1)).delete(activity);
        verify(activityDBController,times(1)).getInternalDB();
    }

    @Test
    void importActivity() throws Exception{
        String path ="server/src/main/resources/activities/activities.json";
        String url = "/debug/activities/import";
        File file = new File(path);
        doNothing().when(activityDBController).forceReload(file);
        mockMvc.perform(post(url).content(path).contentType("application/json")).andExpect(status().isOk());

    }
}