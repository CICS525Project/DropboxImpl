package userGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.EtchedBorder;

import dataTransfer.configurationData;

public class ToolTip {

	private int width = 300;
	private int height = 100;
	private int step = 30;
	private int stepTime = 30;
	private int displayTime = 6000;
	private int countOfToolTip = 0;
	private int maxToolTip = 0;
	private int maxToolTipScreen;
	private Font font;
	private Color bgColor;
	private Color border;
	private Color messageColor;

	int gap;
	boolean useTop = true;

	public ToolTip() {
		font = new Font("Arial", 0, 12);
		bgColor = new Color(255, 255, 255);
		border = Color.BLACK;
		useTop = true;
		try {
			JWindow.class.getMethod("setAlwaysOnTop",
					new Class[] { Boolean.class });
		} catch (Exception e) {
			// TODO: handle exception
			useTop = false;
		}
	}

	class ToolTipSingle extends JWindow {
		private static final long serialVersionUID = 1L;
		private JLabel iconLabel = new JLabel();
		private JTextArea message = new JTextArea();

		public ToolTipSingle() {
			initComponent();
		}

		private void initComponent() {
			setSize(width, height);
			message.setFont(getMessageFont());
			JPanel externalPanel = new JPanel(new BorderLayout(1, 1));
			externalPanel.setBackground(bgColor);
			JPanel innerPanel = new JPanel(new BorderLayout(getGap(), getGap()));
			innerPanel.setBackground(bgColor);
			message.setBackground(bgColor);
			message.setMargin(new Insets(4, 4, 4, 4));
			message.setLineWrap(true);
			message.setWrapStyleWord(true);
			EtchedBorder etchedBorder = (EtchedBorder) BorderFactory
					.createEtchedBorder();
			externalPanel.setBorder(etchedBorder);
			externalPanel.add(innerPanel);
			message.setForeground(getMessageColor());
			innerPanel.add(iconLabel, BorderLayout.WEST);
			innerPanel.add(message, BorderLayout.CENTER);
			getContentPane().add(externalPanel);
		}

		public void animate() {
			new Animation(this).start();
		}
	}

	class Animation extends Thread {
		ToolTipSingle single;

		public Animation(ToolTipSingle single) {
			this.single = single;
		}

		private void animateVertically(int posx, int startY, int endY)
				throws InterruptedException {
			single.setLocation(posx, startY);
			if (endY < startY) {
				for (int i = startY; i > endY; i -= step) {
					single.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			} else {
				for (int i = startY; i < endY; i += step) {
					single.setLocation(posx, i);
					Thread.sleep(stepTime);
				}
			}
			single.setLocation(posx, endY);
		}

		public void run() {
			try {
				boolean animate = true;
				GraphicsEnvironment ge = GraphicsEnvironment
						.getLocalGraphicsEnvironment();
				Rectangle screenRect = ge.getMaximumWindowBounds();
				int screenHeight = (int) screenRect.height;
				int startYPosition;
				int stopYPosition;
				if (screenRect.y > 0) {
					animate = false;
				}
				maxToolTipScreen = screenHeight / height;
				int posx = (int) screenRect.width - width - 1;
				single.setLocation(posx, screenHeight);
				single.setVisible(true);
				if (useTop) {
					single.setAlwaysOnTop(true);
				}
				if (animate) {
					startYPosition = screenHeight;
					stopYPosition = startYPosition - height - 1;
					if (countOfToolTip > 0) {
						stopYPosition = stopYPosition
								- (maxToolTip % maxToolTipScreen * height);
					} else {
						maxToolTip = 0;
					}
				} else {
					startYPosition = screenRect.y - height;
					stopYPosition = screenRect.y;

					if (countOfToolTip > 0) {
						stopYPosition = stopYPosition
								+ (maxToolTip % maxToolTipScreen * height);
					} else {
						maxToolTip = 0;
					}
				}
				countOfToolTip++;
				maxToolTip++;
				animateVertically(posx, startYPosition, stopYPosition);
				Thread.sleep(displayTime);
				animateVertically(posx, stopYPosition, startYPosition);
				countOfToolTip--;
				single.setVisible(false);
				single.dispose();
			} catch (Exception e) {
				// TODO: handle exception
				throw new RuntimeException(e);
			}
		}
	}

	public void setToolTip(Icon icon, String msg) {
		ToolTipSingle single = new ToolTipSingle();
		if (icon != null) {
			single.iconLabel.setIcon(icon);
		}
		single.message.setText(msg);
		single.animate();
	}

	public void setToolTip(String msg) {
		setToolTip(null, msg);
	}

	public Font getMessageFont() {
		return font;
	}

	public void setMessageFont(Font font) {
		this.font = font;
	}

	public Color getBorderColor() {
		return border;
	}

	public void setBorderColor(Color borderColor) {
		this.border = borderColor;
	}

	public int getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(int displayTime) {
		this.displayTime = displayTime;
	}

	public int getGap() {
		return gap;
	}

	public void setGap(int gap) {
		this.gap = gap;
	}

	public Color getMessageColor() {
		return messageColor;
	}

	public void setMessageColor(Color messageColor) {
		this.messageColor = messageColor;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int _step) {
		this.step = _step;
	}

	public int getStepTime() {
		return stepTime;
	}

	public void setStepTime(int _stepTime) {
		this.stepTime = _stepTime;
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ToolTip tip = new ToolTip();
        tip.setToolTip(new ImageIcon(configurationData.DOWN_IMG),"warning warning warning!!!");
	}
}
