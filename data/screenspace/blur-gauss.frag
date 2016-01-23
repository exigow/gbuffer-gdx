uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform vec2 offset;

void main() {
	vec3 sum = vec3(0);
	sum += texture2D(u_texture, v_texCoords + offset * -10).xyz * .009167927656011385;
	sum += texture2D(u_texture, v_texCoords + offset *  -9).xyz * .014053461291849008;
	sum += texture2D(u_texture, v_texCoords + offset *  -8).xyz * .020595286319257878;
	sum += texture2D(u_texture, v_texCoords + offset *  -7).xyz * .028855245532226279;
	sum += texture2D(u_texture, v_texCoords + offset *  -6).xyz * .038650411513543079;
	sum += texture2D(u_texture, v_texCoords + offset *  -5).xyz * .049494378859311142;
	sum += texture2D(u_texture, v_texCoords + offset *  -4).xyz * .060594058578763078;
	sum += texture2D(u_texture, v_texCoords + offset *  -3).xyz * .070921288047096992;
	sum += texture2D(u_texture, v_texCoords + offset *  -2).xyz * .079358891804948081;
	sum += texture2D(u_texture, v_texCoords + offset *  -1).xyz * .084895951965930902;
	sum += texture2D(u_texture, v_texCoords + offset *   0).xyz * .086826196862124602;
	sum += texture2D(u_texture, v_texCoords + offset *  +1).xyz * .084895951965930902;
	sum += texture2D(u_texture, v_texCoords + offset *  +2).xyz * .079358891804948081;
	sum += texture2D(u_texture, v_texCoords + offset *  +3).xyz * .070921288047096992;
	sum += texture2D(u_texture, v_texCoords + offset *  +4).xyz * .060594058578763078;
	sum += texture2D(u_texture, v_texCoords + offset *  +5).xyz * .049494378859311142;
	sum += texture2D(u_texture, v_texCoords + offset *  +6).xyz * .038650411513543079;
	sum += texture2D(u_texture, v_texCoords + offset *  +7).xyz * .028855245532226279;
	sum += texture2D(u_texture, v_texCoords + offset *  +8).xyz * .020595286319257878;
	sum += texture2D(u_texture, v_texCoords + offset *  +9).xyz * .014053461291849008;
	sum += texture2D(u_texture, v_texCoords + offset * +10).xyz * .009167927656011385;
	gl_FragColor = vec4(sum, 1);
}

