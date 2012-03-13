/*
 * Copyright (C) 2010 The Android Open Source Project
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
package com.example.android.nfc.simulator;

/**
 * This class provides a list of fake NFC Ndef format Tags.
 */
public class MockNdefMessages 
{

    /**
     * A plain text tag in English.
     */
    
    public static final byte[] ENGLISH_PLAIN_TEXT =
        new byte[] {(byte) 0xd1, (byte) 0x01, (byte) 0x1c, (byte) 0x54, (byte) 0x02, (byte) 0x65,
            (byte) 0x6e, (byte) '6', (byte) '8', (byte) '9', (byte) '.', (byte) '6',
            (byte) '3', (byte) '2', (byte) '\n', (byte) '1', (byte) '3', (byte) '1',
            (byte) '.', (byte) '7', (byte) '4', (byte) '9', (byte) '\n', (byte) '3',
            (byte) '2', (byte) '4', (byte) '9', (byte) '4', (byte) '5', (byte) '5',
            (byte) '7', (byte) '3'};

    /**
     * All the mock Ndef tags.
     */
    public static final byte[][] ALL_MOCK_MESSAGES =
        new byte[][] {ENGLISH_PLAIN_TEXT};
}
