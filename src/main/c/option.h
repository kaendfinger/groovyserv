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

#ifndef _OPTION_H
#define _OPTION_H

#include "bool.h"

#define MAX_MASK 10
#define CLIENT_OPTION_PREFIX "-C"
#define PORT_NOT_SPECIFIED -1

struct option_t {
    char* host;
    int port;
    char* authtoken;
    BOOL kill;
    BOOL restart;
    BOOL quiet;
    BOOL env_all;
    char* env_include_mask[MAX_MASK];
    char* env_exclude_mask[MAX_MASK];
    BOOL help;
    BOOL version;
};

enum OPTION_TYPE {
    OPT_HOST,
    OPT_PORT,
    OPT_AUTHTOKEN,
    OPT_KILL_SERVER,
    OPT_RESTART_SERVER,
    OPT_QUIET,
    OPT_ENV,
    OPT_ENV_ALL,
    OPT_ENV_EXCLUDE,
    OPT_HELP,
    OPT_VERSION,
};

struct option_info_t {
    char* name;
    enum OPTION_TYPE type;
    BOOL take_value;
};

extern struct option_t client_option;

void usage();
void version();
void scan_options(struct option_t* option, int argc, char **argv);
void print_client_options(struct option_t *opt);

#endif
