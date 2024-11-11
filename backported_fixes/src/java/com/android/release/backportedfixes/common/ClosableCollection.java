/*
 * Copyright (C) 2024 The Android Open Source Project
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
package com.android.release.backportedfixes.common;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/** An AutoCloseable holder for a collection of AutoCloseables. */
public final class ClosableCollection<T extends AutoCloseable,
        C extends Collection<T>> implements
        AutoCloseable {
    C source;

    /** Makes the collection AutoCloseable.*/
    public static <T extends AutoCloseable, C extends Collection<T>> ClosableCollection<T, C> wrap(
            C source) {
        return new ClosableCollection<>(source);
    }

    private ClosableCollection(C source) {
        this.source = source;
    }

    public C getCollection() {
        return source;
    }

    /**
     * Closes each item the collection.
     *
     * @throws Exception if any close throws an an exception, a new exception is thrown with
     *   all the exceptions frp, closing the streams added as suppressed Exceptions.
     */
    @Override
    public void close() throws Exception {
        var list = ImmutableList.<Exception>builder();
        for (T t : source) {
            try {
                t.close();
            } catch (Exception e) {
                list.add(e);
            }
        }
        var result = list.build();
        if (!result.isEmpty()) {
            Exception e = new Exception(
                    "%d of %d failed while closing".formatted(result.size(), source.size()));
            result.forEach(e::addSuppressed);
            throw e;
        }

    }
}
