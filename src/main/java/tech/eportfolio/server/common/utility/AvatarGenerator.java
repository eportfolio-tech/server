package tech.eportfolio.server.common.utility;

import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.GitHubAvatar;
import com.talanlabs.avatargenerator.layers.backgrounds.ColorPaintBackgroundLayer;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class AvatarGenerator {
    public InputStream generateGithubAvatar() {
        Avatar avatar = GitHubAvatar.newAvatarBuilder().layers(new ColorPaintBackgroundLayer(Color.WHITE)).build();
        return new ByteArrayInputStream(avatar.createAsPngBytes(System.currentTimeMillis()));
    }
}
