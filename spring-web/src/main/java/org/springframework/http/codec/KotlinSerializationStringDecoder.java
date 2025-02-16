/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.codec;

import java.util.List;
import java.util.Map;

import kotlinx.serialization.KSerializer;
import kotlinx.serialization.StringFormat;
import org.jspecify.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeType;

/**
 * Abstract base class for {@link Decoder} implementations that defer to Kotlin
 * {@linkplain StringFormat string serializers}.
 *
 * @author Sebastien Deleuze
 * @author Iain Henderson
 * @author Arjen Poutsma
 * @since 6.0
 * @param <T> the type of {@link StringFormat}
 */
public abstract class KotlinSerializationStringDecoder<T extends StringFormat> extends KotlinSerializationSupport<T>
	implements Decoder<Object> {

	// String decoding needed for now, see https://github.com/Kotlin/kotlinx.serialization/issues/204 for more details
	private final StringDecoder stringDecoder = StringDecoder.allMimeTypes(StringDecoder.DEFAULT_DELIMITERS, false);


	public KotlinSerializationStringDecoder(T format, MimeType... supportedMimeTypes) {
		super(format, supportedMimeTypes);
	}

	/**
	 * Configure a limit on the number of bytes that can be buffered whenever
	 * the input stream needs to be aggregated. This can be a result of
	 * decoding to a single {@code DataBuffer},
	 * {@link java.nio.ByteBuffer ByteBuffer}, {@code byte[]},
	 * {@link org.springframework.core.io.Resource Resource}, {@code String}, etc.
	 * It can also occur when splitting the input stream, for example, delimited text,
	 * in which case the limit applies to data buffered between delimiters.
	 * <p>By default this is set to 256K.
	 * @param byteCount the max number of bytes to buffer, or -1 for unlimited
	 */
	public void setMaxInMemorySize(int byteCount) {
		this.stringDecoder.setMaxInMemorySize(byteCount);
	}

	/**
	 * Return the {@link #setMaxInMemorySize configured} byte count limit.
	 */
	public int getMaxInMemorySize() {
		return this.stringDecoder.getMaxInMemorySize();
	}

	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		return canSerialize(elementType, mimeType);
	}

	@Override
	public List<MimeType> getDecodableMimeTypes() {
		return supportedMimeTypes();
	}

	@Override
	public List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
		return supportedMimeTypes();
	}

	@Override
	public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Flux.defer(() -> {
			KSerializer<Object> serializer = serializer(elementType);
			if (serializer == null) {
				return Mono.error(new DecodingException("Could not find KSerializer for " + elementType));
			}
			return this.stringDecoder
					.decode(inputStream, elementType, mimeType, hints)
					.handle((string, sink) -> {
						try {
							sink.next(format().decodeFromString(serializer, string));
						}
						catch (IllegalArgumentException ex) {
							sink.error(processException(ex));
						}
					});
		});
	}

	@Override
	public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType,
			@Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
		return Mono.defer(() -> {
			KSerializer<Object> serializer = serializer(elementType);
			if (serializer == null) {
				return Mono.error(new DecodingException("Could not find KSerializer for " + elementType));
			}
			return this.stringDecoder
					.decodeToMono(inputStream, elementType, mimeType, hints)
					.handle((string, sink) -> {
						try {
							sink.next(format().decodeFromString(serializer, string));
							sink.complete();
						}
						catch (IllegalArgumentException ex) {
							sink.error(processException(ex));
						}
					});
		});
	}

	private CodecException processException(IllegalArgumentException ex) {
		return new DecodingException("Decoding error: " + ex.getMessage(), ex);
	}

}
