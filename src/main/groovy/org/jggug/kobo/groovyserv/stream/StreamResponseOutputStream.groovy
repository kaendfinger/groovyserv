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
package org.jggug.kobo.groovyserv.stream

import org.jggug.kobo.groovyserv.ClientConnection
import org.jggug.kobo.groovyserv.ClientProtocols
import org.jggug.kobo.groovyserv.utils.LogUtils

/**
 * Handling StreamResponse in protocol between client and server.
 *
 * @author NAKANO Yasuharu
 */
class StreamResponseOutputStream extends OutputStream {

    private OutputStream outputStream
    private String streamId
    private boolean closed = false
    private boolean noHeader = false

    private StreamResponseOutputStream() { /* preventing from instantiation */ }

    static OutputStream newOut(OutputStream outputStream, boolean noHeader = false) {
        new StreamResponseOutputStream(streamId: 'out', outputStream: outputStream, noHeader: noHeader)
    }

    static OutputStream newErr(OutputStream outputStream, boolean noHeader = false) {
        new StreamResponseOutputStream(streamId: 'err', outputStream: outputStream, noHeader: noHeader)
    }

    /**
     * @throws IOException When the stream is already closed
     */
    @Override
    void write(int b) {
        if (closed) throw new IOException("Stream of channel '$streamId' already closed")
        byte[] buf = new byte[1]
        buf[0] = (b >> 8 * 0) & 0x0000ff
        write(buf, 0, 1)
    }

    /**
     * @throws IOException When the stream is already closed
     */
    @Override
    void write(byte[] b, int offset, int length) {
        if (closed) throw new IOException("Stream of channel '$streamId' already closed")
        writeVerboseLog(b, offset, length)
        // FIXME When System.exit to a sub thread which in infinte loop, following synchronized occures IllegalMonitorStateException.
        //synchronized(outputStream) { // to keep independency of 'out' and 'err' on socket stream
        byte[] header = ClientProtocols.formatAsResponseHeader(streamId, length)
        outputStream.with {
            if (!noHeader) write(header)
            write(b, offset, length)
            flush()
        }
        //}
    }

    /**
     * @throws IOException When the stream is already closed
     */
    @Override
    void flush() {
        if (closed) throw new IOException("Stream of channel '$streamId' already closed")
        outputStream.flush()
    }

    @Override
    void close() {
        closed = true
        LogUtils.debugLog "StreamResponseOutputStream($streamId) is closed: ${ClientConnection.currentConnection}"
    }

    private writeVerboseLog(byte[] b, int offset, int length) {
        LogUtils.debugLog {
            """\
            |>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            |Server->Client {
            |  id: ${streamId}
            |  size(actual): ${length}
            |  thread group: ${Thread.currentThread().threadGroup.name}
            |  body:
            |${LogUtils.dumpHex(b, offset, length)}
            |}
            |<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            |""".stripMargin()
        }
    }

}

