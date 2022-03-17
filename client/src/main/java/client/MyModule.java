/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.*;

import client.utils.ServerUtils;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * The module used by the injector for the FXML loader, that makes sure there exists only one of each controller
 */
public class MyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MainScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(GeneralQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ComparisonQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EstimationQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(UserCtrl.class).in(Scopes.SINGLETON);
        binder.bind(WaitingRoomCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AdminCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AdminEditActivityCtrl.class).in(Scopes.SINGLETON);
    }
}