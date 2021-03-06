/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _CONFIG_H
#define _CONFIG_H

#if defined(_MSC_VER) || defined(__MINGW32__)
#define WINDOWS
#else
#define UNIX
#endif

#define SERVER_HOST "localhost"
#define SERVER_PORT 1961

#define ERROR_INVALID_AUTHTOKEN 201
#define ERROR_CLIENT_NOT_ALLOWED 202

#endif
