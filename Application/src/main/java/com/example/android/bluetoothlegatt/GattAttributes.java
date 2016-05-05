/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    // Service
    public static String ACCELERATION_SERVICE = "0000ffa0-0000-1000-8000-00805f9b34fb";

    // Characteristic
    public static String ENABLE_ACCELERATION_CHARACTERISTIC = "0000ffa1-0000-1000-8000-00805f9b34fb";
    public static String USER_PIN_CHARACTERISTIC = "0000ffb0-0000-1000-8000-00805f9b34fb";


    static {
        attributes.put(ACCELERATION_SERVICE, "ACCELERATION SERVICE");
        attributes.put(ENABLE_ACCELERATION_CHARACTERISTIC, "ENABLE ACCELERATION CHARACTERISTIC");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
