/*
 * Copyright @ 2015 Atlassian Pty Ltd
 * Copyright @ 2017 Alianza, Inc.
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

package org.jitsi.impl.neomedia.jmfext.media.protocol.wavfile;

import com.musicg.wave.Wave;
import net.sf.fmj.utility.IOUtils;
import org.jitsi.impl.neomedia.jmfext.media.protocol.AbstractPullBufferStream;
import org.jitsi.impl.neomedia.recording.RTPRecorder;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.control.FormatControl;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Implements a <tt>PullBufferStream</tt> which read a wav file
 *
 * @author Thomas Kuntz
 * @author Mike Saavedra
 */
public class WavStream extends AbstractPullBufferStream<DataSource> {

    protected Wave wave;
//    protected AudioInputStream wavInputStream;

    /**
     * Initializes a new <tt>WavStream</tt> instance which is to have a specific <tt>FormatControl</tt>
     *
     * @param dataSource the <tt>DataSource</tt> which is creating the new
     * instance so that it becomes one of its <tt>streams</tt>
     * @param formatControl the <tt>FormatControl</tt> of the new instance which
     * is to specify the format in which it is to provide its media data
     */
    WavStream(DataSource dataSource, FormatControl formatControl) {
        super(dataSource, formatControl);
        String filePath = dataSource.getLocator().getRemainder();
        wave = new Wave(filePath);
//        try {
//            wavInputStream = AudioSystem.getAudioInputStream(new File(filePath));
//            AudioFormat format = wavInputStream.getFormat();
//            long frameLength = wavInputStream.getFrameLength();
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Reads available media data from this instance into a specific <tt>Buffer</tt>.
     *
     * @param buffer the <tt>Buffer</tt> to write the available media data into
     * @throws IOException if an I/O error has prevented the reading of
     * available media data from this instance into the specified <tt>buffer</tt>
     */
    @Override
    public void read(Buffer buffer) throws IOException {
        if (buffer == null) {
            return;
        }

        Format format = buffer.getFormat();
        if (format == null)
        {
            format = getFormat();
            if (format != null)
                buffer.setFormat(format);
        }

//        AudioFormat wavFormat = wavInputStream.getFormat();
//        long frameLength = wavInputStream.getFrameLength();

        byte[] waveBytes = wave.getBytes();
//        byte[] wisBytes = IOUtils.readAll(wavInputStream);
        RTPRecorder.saveBytesToFile(waveBytes, "WavStream.read.waveBytes.raw");
//        RTPRecorder.saveBytesToFile(wisBytes, "WavStream.read.wisBytes.raw");
        buffer.setData(waveBytes);
//        buffer.setData(wisBytes);
        buffer.setOffset(0);
        buffer.setLength(waveBytes.length);
//        buffer.setLength(wisBytes.length);

        buffer.setTimeStamp(System.nanoTime());
        buffer.setFlags(Buffer.FLAG_SYSTEM_TIME | Buffer.FLAG_LIVE_DATA);
    }

}
